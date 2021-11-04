// CacheManager for Flutter
// Copyright (c) 2017 Rene Floor
// Released under MIT License.
library flutter_cache_manager;



import 'dart:io';

import 'package:fPix/com/longforus/fPix/db/OB.dart';
import 'package:fPix/com/longforus/fPix/bean/cache_object.dart';
import 'package:fPix/objectbox.g.dart';
import 'package:flutter/widgets.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:synchronized/synchronized.dart';

class CacheManager {
  static const _keyCacheCleanDate = "lib_cached_image_data_last_clean";

  static Duration inBetweenCleans = new Duration(days: 7);
  static Duration maxAgeCacheObject = new Duration(days: 30);
  static int maxNrOfCacheObjects = 200;
  static bool showDebugLogs = true;

  static CacheManager _instance;

  static Future<CacheManager> getInstance() async {
    if (_instance == null) {
      await _lock.synchronized(() async {
        if (_instance == null) {
          // keep local instance till it is fully initialized
          var newInstance = new CacheManager._();
          await newInstance._init();
          _instance = newInstance;
        }
      });
    }
    return _instance;
  }

  CacheManager._();

  SharedPreferences _prefs;
  Box<CacheObject> _box;
  DateTime lastCacheClean;

  static Lock _lock = new Lock();

  ///Shared preferences is used to keep track of the information about the files
  Future _init() async {
    _prefs = await SharedPreferences.getInstance();
    _box =  OB.getInstance().store.box();
    _getLastCleanTimestampFromPreferences();
  }

  bool _isStoringData = false;
  bool _shouldStoreDataAgain = false;
  Lock _storeLock = new Lock();


  ///Store all data to shared preferences
  _save() async {
    if (!(await _canSave())) {
      return;
    }

    await _cleanCache();
    await _saveDataInPrefs();
  }

  Future<bool> _canSave() async {
    return await _storeLock.synchronized(() {
      if (_isStoringData) {
        _shouldStoreDataAgain = true;
        return false;
      }
      _isStoringData = true;
      return true;
    });
  }

  Future<bool> _shouldSaveAgain() async {
    return await _storeLock.synchronized(() {
      if (_shouldStoreDataAgain) {
        _shouldStoreDataAgain = false;
        return true;
      }
      _isStoringData = false;
      return false;
    });
  }

  _saveDataInPrefs() async {
    if (await _shouldSaveAgain()) {
      await _saveDataInPrefs();
    }
  }

  _getLastCleanTimestampFromPreferences() {
    // Get data about when the last clean action has been performed
    var cleanMillis = _prefs.getInt(_keyCacheCleanDate);
    if (cleanMillis != null) {
      lastCacheClean = new DateTime.fromMillisecondsSinceEpoch(cleanMillis);
    } else {
      lastCacheClean = new DateTime.now();
      _prefs.setInt(_keyCacheCleanDate, lastCacheClean.millisecondsSinceEpoch);
    }
  }

  _cleanCache({force: false}) async {
    var sinceLastClean = new DateTime.now().difference(lastCacheClean);

    if (force ||
        sinceLastClean > inBetweenCleans ||
        _box.count() > maxNrOfCacheObjects) {
      await _lock.synchronized(() async {
        await _removeOldObjectsFromCache();
        await _shrinkLargeCache();

        lastCacheClean = new DateTime.now();
        _prefs.setInt(
            _keyCacheCleanDate, lastCacheClean.millisecondsSinceEpoch);
      });
    }
  }

  _removeOldObjectsFromCache() async {
    var oldestDateAllowed = new DateTime.now().subtract(maxAgeCacheObject).millisecondsSinceEpoch;
    //Remove old objects
    List<CacheObject> oldValues = _box.query(CacheObject_.touched.lessThan(oldestDateAllowed)).build().find();
    _box.removeMany(oldValues.map((e) => e.id));
    for (var oldValue in oldValues) {
      await _removeFile(oldValue);
    }
  }

  _shrinkLargeCache() async {
    //Remove oldest objects when cache contains to many items
    if (_box.count() > maxNrOfCacheObjects) {
      final query = (_box.query()..order(CacheObject_.touched)).build()
        ..limit =( _box.count()-maxNrOfCacheObjects);
      List<CacheObject> oldestValues = query.find();
      _box.removeMany(oldestValues.map((e) => e.id));
      oldestValues.forEach((item) async {
        await _removeFile(item);
      }); //remove them
    }
  }

  _removeFile(CacheObject cacheObject) async {
    //Ensure the file has been downloaded
    if (cacheObject.relativePath == null) {
      return;
    }
    _box.remove(cacheObject.id);
    var file = new File(await cacheObject.getFilePath());
    if (await file.exists()) {
      file.delete();
    }
  }

  ///Get the file from the cache or online. Depending on availability and age
  Future<File> getFile(String url,
      {String cacheKey, Map<String, String> headers}) async {
    String log = "[Flutter Cache Manager] Loading $url";
    debugPrint(log);
    if(cacheKey==null||cacheKey.isEmpty) {
      cacheKey = url;
    }
    assert(_box!=null);
    CacheObject cacheObject = _box.query(CacheObject_.cacheKey.equals(cacheKey)).build().findFirst();
    if(cacheObject==null) {
      cacheObject = new CacheObject(url:url,cacheKey: cacheKey);
    }
    cacheObject = await _checkCache(cacheObject, headers,url,cacheKey);
    debugPrint("after checkCache ${cacheObject.toString()}");
    //If non of the above is true, than we don't have to download anything.
    _save();
    if (showDebugLogs) print(log);
    if(cacheObject==null) {
      return null;
    }
    var path = await cacheObject.getFilePath();
    if (path == null) {
      return null;
    }
    return new File(path);
  }

  Future<CacheObject> _checkCache(CacheObject cacheObject,Map<String, String> headers,String url,String cacheKey) async {
    // Set touched date to show that this object is being used recently
    cacheObject.touch();

    if (headers == null) {
      headers = new Map<String, String>();
    }

    var filePath = await cacheObject.getFilePath();
    //If we have never downloaded this file, do download
    if (filePath == null) {
      debugPrint( "Downloading for first time.");
      /// 为什么下面的代码就不执行了??? 因为刚刚上面的 headers = new Map<String, String>();
      /// 是 headers = new Map(); 被赋值为Map类型不是_downloadFile需要的Map<String, String> 报错了,而
      CacheObject newCacheObject = await _downloadFile(url, headers,cacheKey: cacheKey);
      if (newCacheObject != null) {
        _box.put(newCacheObject);
      }
      return newCacheObject;
    }
    //If file is removed from the cache storage, download again
    var cachedFile = new File(filePath);
    var cachedFileExists = await cachedFile.exists();
    if (!cachedFileExists) {
      debugPrint( "Downloading because file does not exist.");
      CacheObject newCacheObject = await _downloadFile(url, headers, relativePath: cacheObject.relativePath,cacheKey: cacheKey);
      if (newCacheObject != null) {
        _box.put(newCacheObject);
      }

      debugPrint( "Cache file valid till ${cacheObject.validTill?.toIso8601String() ?? "only once.. :("}");
      return newCacheObject;
    }
    //If file is old, download if server has newer one
    if (!cacheObject.favorite.hasValue&&(cacheObject.validTill == null ||
        cacheObject.validTill.isBefore(new DateTime.now()))) {
      debugPrint( "Updating file in cache.");
      CacheObject newCacheObject = await _downloadFile(url, headers,
          relativePath: cacheObject.relativePath, eTag: cacheObject.eTag, cacheKey: cacheKey);
      if (newCacheObject != null) {
        _box.put(newCacheObject);
      }
      debugPrint( "New cache file valid till ${cacheObject.validTill?.toIso8601String() ?? "only once.. :("}");
      return newCacheObject;
    }
    debugPrint( "Using file from cache.\nCache valid till ${cacheObject.validTill?.toIso8601String() ?? "only once.. "
        ":("}");
    return cacheObject;
  }


  ///Download the file from the url
  Future<CacheObject> _downloadFile(String url, Map<String, String> headers, {String cacheKey,String relativePath, String eTag}) async {
    if(cacheKey==null||cacheKey.isEmpty) {
      cacheKey = url;
    }
    var newCache = new CacheObject(url:url,cacheKey: cacheKey);
    newCache.setRelativePath(relativePath);

    if (eTag != null) {
      headers["If-None-Match"] = eTag;
    }

    var response;
    try {
      response = await http.get(Uri.parse(url), headers: headers);
    } catch (e) {
      debugPrint( "download error $e");
    }
    if (response != null) {
      switch(response.statusCode){
        case 200:
          await newCache.setDataFromHeaders(response.headers);

          var filePath = await newCache.getFilePath();
          var folder = new File(filePath).parent;
          if (!(await folder.exists())) {
            folder.createSync(recursive: true);
          }
          await new File(filePath).writeAsBytes(response.bodyBytes);
          return newCache;
          break;

        case 304:
          await newCache.setDataFromHeaders(response.headers);
          return newCache;
          break;
        case 400:
          _box.remove(newCache.id);
          break;
      }
    }
    return null;
  }
}
