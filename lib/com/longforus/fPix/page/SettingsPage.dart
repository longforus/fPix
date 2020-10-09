import 'package:fPix/com/longforus/fPix/utils/FileManager.dart';
import 'package:fPix/com/longforus/fPix/view/DirSelector.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:url_launcher/url_launcher.dart';

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
  static const githubUrl = 'https://github.com/longforus';

  void _getImgDownloadDir() async {
    var manager = await FileManager.get(context);
    manager.getImgDownloadDir().then((dir) {
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
    const bulueTestStyle = TextStyle(color: Colors.lightBlue);
    return Scaffold(
      appBar: AppBar(
        leading: new Icon(Icons.settings),
        title: new Text('Settings'),
      ),
      body: Column(
        children: <Widget>[
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
                    Expanded(
                        child: Text(
                      currentImgDownloadDir,
                      textAlign: TextAlign.start,
                      style: TextStyle(
                        fontSize: 14,
                      ),
                      softWrap: true,
                    )),
                    FlatButton.icon(
                      onPressed: _onChangeImageDownloadDir,
                      icon: Icon(Icons.image,color: Colors.lightBlue,),
                      label: Text(
                        "change",
                        textAlign: TextAlign.end,
                        style: bulueTestStyle,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          Container(
            child: new Column(
              children: <Widget>[
                new Text(
                  'Author:longforus',
                  textAlign: TextAlign.center,
                ),
                new Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    new Text('github:'),
                    new GestureDetector(
                      child: new Text(
                        githubUrl,
                        style: new TextStyle(color: Colors.blue, decoration: TextDecoration.underline),
                      ),
                      onTap: onGo2Github,
                    )
                  ],
                )
              ],
              mainAxisAlignment: MainAxisAlignment.end,
            ),
            margin: EdgeInsets.only(bottom: 30),
          )
        ],
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
      ),
    );
  }

  void _onChangeImageDownloadDir() async {
    String path = await Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) {
      return new DirSelector();
    }));
    if (path != null && path.isNotEmpty) {
      setState(() {
        currentImgDownloadDir = path;
      });
    }
  }

  void onGo2Github() async {
    if (await canLaunch(githubUrl)) {
      await launch(githubUrl);
    } else {
      throw 'Could not launch $githubUrl';
    }
  }
}
