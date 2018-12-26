import 'package:fPix/com/longforus/fPix/utils/FileManager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({
    Key key,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new _SettingsPageState();
  }
}

class _SettingsPageState extends State<SettingsPage> {
  String currentImgDownloadDir = "";

  void _getImgDownloadDir() {
    FileManager.get(context).getImgDownloadDir().then((dir){
      setState(() {
        currentImgDownloadDir = dir;
      });
    });

  }

  @override
  void initState() {
    _getImgDownloadDir();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: <Widget>[
        AppBar(
          leading: new Icon(Icons.settings),
          title: new Text('Settings'),
        ),
        new Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            new Container(
              padding: EdgeInsets.all(8.0),
              child: Text(
                'Image download dir:',
                textAlign: TextAlign.start,
                style: TextStyle(
                  fontSize: 15,
                ),
              ),
            ),
            new Container(
              padding: EdgeInsets.fromLTRB(12, 4, 12, 8),
              child: new Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  Text(
                    currentImgDownloadDir,
                    textAlign: TextAlign.start,
                    style: TextStyle(
                      fontSize: 14,
                    ),
                  ),
                  GestureDetector(
                    onTap: _onChangeImageDownloadDir,
                    child: Text(
                      "change",
                      textAlign: TextAlign.end,
                      style: TextStyle(
                          fontSize: 14,
                          color: Colors.blue
                      ),
                    ),
                  ),
                ],
              ),
            )
          ],
        )
      ],
    );
  }

  void _onChangeImageDownloadDir() {
    //todo change dir
  }
}
