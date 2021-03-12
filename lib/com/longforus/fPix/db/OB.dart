import 'dart:io';

import 'package:fPix/objectbox.g.dart';
import 'package:path_provider/path_provider.dart';
import 'package:synchronized/synchronized.dart';
class OB{
  static OB _instance;
  static Lock _lock = new Lock();
  static OB getInstance()  {
    if (_instance == null) {
       _lock.synchronized(()  {
        if (_instance == null) {
          var newInstance = new OB._();
          _instance = newInstance;
           _instance._init();
        }
      });
    }
    return _instance;
  }

  OB._();
  Store store;

  _init() async {
    Directory dir =  await getApplicationDocumentsDirectory();
    // Note: getObjectBoxModel() is generated for you in objectbox.g.dart
    store = Store(getObjectBoxModel(), directory: dir.path + '/objectbox');
  }

  dispose(){
    store?.close();
  }

}