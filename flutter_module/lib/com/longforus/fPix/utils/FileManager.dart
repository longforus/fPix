import 'dart:async';
import 'dart:io';

import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/utils/Toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:simple_permissions/simple_permissions.dart';

/// @describe
/// @author  XQ Yang
/// @date 12/26/2018  10:57 AM

class FileManager {
  static FileManager _manager;

  static Future<FileManager> get(BuildContext context) async {
    if (_manager == null) {
      _manager = new FileManager();
      bool result = await _manager._checkPermissions(context);
      if (!result) {
        return null;
      }
    }
    return _manager;
  }

  Future<File> save2SdCard(File file) async {
    String savePath = await getImgDownloadDir();
    var fileName = file.path.substring(file.path.lastIndexOf('/') + 1);
    var save2File = new File('$savePath/$fileName');
    save2File.exists().then((exists) async {
      if (!exists) {
        save2File.createSync(recursive: true);
      }
      return await file.copy(save2File.path);
    });
    return save2File;
  }

  Future<String> getImgDownloadDir() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var savePath = prefs.getString(DEFAULT_IMG_SAVE_PATH_KEY);
    if (savePath == null || savePath.isEmpty) {
      var sDCardDir = (await getExternalStorageDirectory()).path;
      savePath = "$sDCardDir/fPix/image";
    }
    return savePath;
  }

  void setImgDownloadDir(String path) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString(DEFAULT_IMG_SAVE_PATH_KEY, path);
  }

  Future<bool> _checkPermissions(BuildContext context) async {
    bool permission1 = await SimplePermissions.checkPermission(Permission.ReadExternalStorage);
    bool permission2 = await SimplePermissions.checkPermission(Permission.WriteExternalStorage);
    if (!permission1) {
      PermissionStatus status = await SimplePermissions.requestPermission(Permission.ReadExternalStorage);
      switch (status) {
        case PermissionStatus.deniedNeverAsk:
        case PermissionStatus.notDetermined:
        case PermissionStatus.restricted:
        case PermissionStatus.denied:
          Toast.toast(context, "权限被拒绝");
          break;
        case PermissionStatus.authorized:
          permission1 = true;
      }
    }
    if (!permission2) {
      PermissionStatus status = await SimplePermissions.requestPermission(Permission.WriteExternalStorage);
      switch (status) {
        case PermissionStatus.deniedNeverAsk:
        case PermissionStatus.notDetermined:
        case PermissionStatus.restricted:
        case PermissionStatus.denied:
          Toast.toast(context, "权限被拒绝");
          break;
        case PermissionStatus.authorized:
          permission2 = true;
      }
    }
    return permission1 && permission2;
  }
}
