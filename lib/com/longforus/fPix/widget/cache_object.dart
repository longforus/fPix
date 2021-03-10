// CacheManager for Flutter
// Copyright (c) 2017 Rene Floor
// Released under MIT License.

import 'dart:async';
import 'dart:io';

import 'package:objectbox/objectbox.dart';
import 'package:path_provider/path_provider.dart';
import 'package:synchronized/synchronized.dart';
import 'package:uuid/uuid.dart';

///Cache information of one file
///
@Entity()
class CacheObject {

    Future<String> getFilePath() async {
        if (relativePath == null) {
            return null;
        }
        Directory directory = await getTemporaryDirectory();
        return directory.path + relativePath;
    }

    String relativePath;
    DateTime validTill;
    String eTag;
    DateTime touched;
    String url;
    String cacheKey;
    @Transient()
    Lock lock;
    @Id(assignable: true)
    int id;


    CacheObject({String url, this.cacheKey, this.lock}) {
        this.url = url;
        if (cacheKey == null || cacheKey.isEmpty) {
            cacheKey = url;
        }
        id = cacheKey.hashCode;
        touch();
        if (lock == null) {
            lock = new Lock();
        }
    }



    touch() {
        touched = new DateTime.now();
    }

    setDataFromHeaders(Map<String, String> headers) async {
        //Without a cache-control header we keep the file for a week
        var ageDuration = new Duration(days: 7);

        if (headers.containsKey("cache-control")) {
            var cacheControl = headers["cache-control"];
            var controlSettings = cacheControl.split(", ");
            controlSettings.forEach((setting) {
                if (setting.startsWith("max-age=")) {
                    var validSeconds =
                    int.parse(setting.split("=")[1], onError: (source) => 0);
                    if (validSeconds > 0) {
                        ageDuration = new Duration(seconds: validSeconds);
                    }
                }
            });
        }

        validTill = new DateTime.now().add(ageDuration);

        if (headers.containsKey("etag")) {
           eTag = headers["etag"];
        }

        var fileExtension = "";
        if (headers.containsKey("content-type")) {
            var type = headers["content-type"].split("/");
            if (type.length == 2) {
                fileExtension = ".${type[1]}";
            }
        }

        var oldPath = await getFilePath();
        if (oldPath != null && !oldPath.endsWith(fileExtension)) {
            removeOldFile(oldPath);
            relativePath = null;
        }

        if (relativePath == null) {
            var fileName = "/${new Uuid().v1()}$fileExtension";
            relativePath = "$fileName";
        }
    }

    removeOldFile(String filePath) async {
        var file = new File(filePath);
        if (await file.exists()) {
            await file.delete();
        }
    }

    setRelativePath(String path) {
        relativePath = path;
    }
}
