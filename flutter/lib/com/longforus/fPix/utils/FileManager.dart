import 'dart:async';
import 'dart:core';
import 'dart:io';

import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/utils/fpix_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:mmkv/mmkv.dart';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';

/// @describe
/// @author  XQ Yang
/// @date 12/26/2018  10:57 AM

class FileManager {
    static FileManager? _manager;

    static Future<FileManager?> get(BuildContext context) async {
        if (_manager == null) {
            _manager = new FileManager();
            bool result = await _manager!._checkPermissions(context);
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
        MMKV prefs = MMKV.defaultMMKV();
        var savePath = prefs.decodeString(DEFAULT_IMG_SAVE_PATH_KEY);
        if (savePath == null || savePath.isEmpty) {
            var sDCardDir = (await getExternalStorageDirectory())!.path;
            savePath = "$sDCardDir/fPix/image";
        }
        return savePath;
    }

    void setImgDownloadDir(String path) async {
        MMKV prefs = MMKV.defaultMMKV();
        prefs.encodeString(DEFAULT_IMG_SAVE_PATH_KEY, path);
    }

    Future<bool> _checkPermissions(BuildContext context) async {
        var status = await Permission.storage.request();
        if (!status.isGranted) {
            shortToast( "权限被拒绝");
        }
        return status.isGranted;
    }

}
