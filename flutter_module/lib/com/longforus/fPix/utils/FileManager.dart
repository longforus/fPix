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

  static FileManager get(BuildContext context) {
    if (_manager == null) {
      _manager = new FileManager();
      _manager._checkPermissions(context);
    }
    return _manager;
  }

  void save2SdCard(File file) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    var savePath = prefs.getString(DEFAULT_IMG_SAVE_PATH_KEY);
    if (savePath == null || savePath.isEmpty) {
      var sDCardDir = (await getExternalStorageDirectory()).path;
      savePath = "$sDCardDir/fPix/image";
    }
    var fileName = file.path.substring(file.path.lastIndexOf('/') + 1);
    print('$fileName');
    var save2File = new File('$savePath/$fileName');
    save2File.exists().then((exists) {
      if (!exists) {
        save2File.createSync(recursive: true);
      }
      file.copy(save2File.path);
    });
  }

  void _checkPermissions(BuildContext context) {
    SimplePermissions.checkPermission(Permission.WriteExternalStorage)
        .then((have) {
      if (!have) {
        SimplePermissions.requestPermission(Permission.WriteExternalStorage)
            .then((status) {
          if (status != PermissionStatus.authorized) {
            Toast.toast(context, '不同意授权无法保存哦!');
          }
        });
      }
    });
  }
}
