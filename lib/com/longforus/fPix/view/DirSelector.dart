import 'package:fPix/com/longforus/fPix/utils/FileManager.dart';
import 'package:fPix/com/longforus/fPix/view/click_effect.dart';
import 'package:flutter/material.dart';
import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';

class DirSelector extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _DirSelectorState();
  }
}

class _DirSelectorState extends State<DirSelector> {
  List<FileSystemEntity> files = [];
  Directory parentDir;
  ScrollController controller = ScrollController();
  int count = 0; // 记录当前文件夹中以 . 开头的文件和文件夹
  String sDCardDir;
  List<double> position = [];

  @override
  void initState() {
    super.initState();
    getPermission();
  }

  // 权限检查与申请
  Future<void> getPermission() async {
    PermissionStatus permission1 = await PermissionHandler().checkPermissionStatus(PermissionGroup.storage);
    switch (permission1) {
      case PermissionStatus.restricted:
      case PermissionStatus.denied:
      case PermissionStatus.disabled:
      case PermissionStatus.unknown:
        Map<PermissionGroup, PermissionStatus> permissions =
            await PermissionHandler().requestPermissions([PermissionGroup.storage]);
        if (permissions[PermissionGroup.storage] == PermissionStatus.granted) {
          getSDCardDir();
        }
        break;
      default:
        getSDCardDir();
    }
  }

  Future<void> getSDCardDir() async {
    sDCardDir = (await getExternalStorageDirectory()).path;
    parentDir = Directory(sDCardDir);
    initDirectory(sDCardDir);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () {
        _onBackPressed(context);
      },
      child: Scaffold(
          appBar: AppBar(
            title: Text(
              parentDir?.path == sDCardDir ? 'SD Card' : parentDir.path.substring(parentDir.parent.path.length + 1),
              style: TextStyle(color: Colors.white),
            ),
            elevation: 0.4,
            centerTitle: true,
            leading: IconButton(
                icon: Icon(
                  Icons.chevron_left,
                  color: Colors.white,
                ),
                onPressed: () {
                  _onBackPressed(context);
                }),
            actions: <Widget>[
              new IconButton(icon: new Icon(Icons.library_add), onPressed: _onNewDir),
              new IconButton(icon: new Icon(Icons.save), onPressed: _onSave)
            ],
          ),
          backgroundColor: Color(0xfff3f3f3),
          body: Scrollbar(
            child: ListView.builder(
              controller: controller,
              itemCount: files.length != 0 ? files.length : 1,
              itemBuilder: (BuildContext context, int index) {
                if (files.length != 0)
                  return buildListViewItem(files[index]);
                else
                  return Padding(
                    padding: EdgeInsets.only(
                        top: MediaQuery.of(context).size.height / 2 - MediaQuery.of(context).padding.top - 56.0),
                    child: Center(
                      child: Text('The folder is empty'),
                    ),
                  );
              },
            ),
          )),
    );
  }

  void _onBackPressed(BuildContext context) {
    if (parentDir.path != sDCardDir) {
      initDirectory(parentDir.parent.path);
      jumpToPosition(false);
    } else {
      Navigator.pop(context);
    }
  }

  // 计算文件夹内 文件、文件夹的数量，以 . 开头的除外
  removePointBegin(Directory path) {
    var dir = Directory(path.path).listSync();
    int num = dir.length;

    for (int i = 0; i < dir.length; i++) {
      if (dir[i].path.substring(dir[i].parent.path.length + 1).substring(0, 1) == '.') num--;
    }
    return num;
  }

  buildListViewItem(FileSystemEntity file) {
    var isFile = FileSystemEntity.isFileSync(file.path);

    // 去除以 . 开头的文件和文件夹
    if (file.path.substring(file.parent.path.length + 1).substring(0, 1) == '.') {
      count++;
      if (count != files.length) {
        return Container();
      } else {
        return Padding(
          padding:
              EdgeInsets.only(top: MediaQuery.of(context).size.height / 2 - MediaQuery.of(context).padding.top - 56.0),
          child: Center(
            child: Text('The folder is empty'),
          ),
        );
      }
    }

    int length = 0;
    if (!isFile) length = removePointBegin(file);

    return ClickEffect(
      child: Column(
        children: <Widget>[
          ListTile(
            leading: Image.asset("images/folder.png"),
            title: Row(
              children: <Widget>[
                Expanded(child: Text(file.path.substring(file.parent.path.length + 1))),
                isFile
                    ? Container()
                    : Text(
                        '$length项',
                        style: TextStyle(color: Colors.grey),
                      )
              ],
            ),
            subtitle: isFile
                ? Text(
                    '${getFileLastModifiedTime(file)}  ${getFileSize(file)}',
                    style: TextStyle(fontSize: 12.0),
                  )
                : null,
            trailing: isFile ? null : Icon(Icons.chevron_right),
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 14.0),
            child: Divider(
              height: 1.0,
            ),
          )
        ],
      ),
      onTap: () {
        position.insert(position.length, controller.offset);
        initDirectory(file.path);
        jumpToPosition(true);
      },
    );
  }

  void jumpToPosition(bool isEnter) {
    if (isEnter)
      controller.jumpTo(0.0);
    else {
      controller.jumpTo(position[position.length - 1]);
      position.removeLast();
    }
  }

  Future<void> initDirectory(String path) async {
    try {
      setState(() {
        var directory = Directory(path);
        count = 0;
        parentDir = directory;
        files.clear();
        files = directory.listSync().takeWhile((e) {
          return FileSystemEntity.isDirectorySync(e.path);
        }).toList();
      });
    } catch (e) {
      print(e);
      print("Directory does not exist！");
    }
  }

  getFileSize(FileSystemEntity file) {
    int fileSize = File(file.resolveSymbolicLinksSync()).lengthSync();
    if (fileSize < 1024) {
      // b
      return '${fileSize.toStringAsFixed(2)}B';
    } else if (1024 <= fileSize && fileSize < 1048576) {
      // kb
      return '${(fileSize / 1024).toStringAsFixed(2)}KB';
    } else if (1048576 <= fileSize && fileSize < 1073741824) {
      // mb
      return '${(fileSize / 1024 / 1024).toStringAsFixed(2)}MB';
    }
  }

  getFileLastModifiedTime(FileSystemEntity file) {
    DateTime dateTime = File(file.resolveSymbolicLinksSync()).lastModifiedSync();

    String time =
        '${dateTime.year}-${dateTime.month < 10 ? 0 : ''}${dateTime.month}-${dateTime.day < 10 ? 0 : ''}${dateTime.day} ${dateTime.hour < 10 ? 0 : ''}${dateTime.hour}:${dateTime.minute < 10 ? 0 : ''}${dateTime.minute}';
    return time;
  }

  void _onNewDir() {
    final TextEditingController _controller = new TextEditingController();
    showDialog(
        context: context,
        builder: (_) => new AlertDialog(
                title: new Text("Create a new dir"),
                content: new TextField(
                  controller: _controller,
                  decoration: new InputDecoration(
                    hintText: 'Type Name',
                  ),
                ),
                actions: <Widget>[
                  new FlatButton(
                    child: new Text("CANCEL"),
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                  ),
                  new FlatButton(
                    child: new Text("OK"),
                    onPressed: () {
                      var directory = new Directory("${parentDir.path}/${_controller.text}");
                      directory.create();
                      initDirectory(directory.path);
                      jumpToPosition(true);
                      Navigator.of(context).pop();
                    },
                  )
                ]));
  }

  void _onSave() async {
    var manager = await FileManager.get(context);
    manager.setImgDownloadDir(parentDir.path);
    Navigator.of(context).pop(parentDir.path);
  }
}
