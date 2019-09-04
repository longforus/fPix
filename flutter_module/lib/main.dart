import 'dart:async';

import 'package:fPix/com/longforus/fPix/page/FavoritePage.dart';
import 'package:fPix/com/longforus/fPix/page/ImagePage.dart';
import 'package:fPix/com/longforus/fPix/page/SettingsPage.dart';
import 'package:fPix/com/longforus/fPix/page/VideoPage.dart';
import 'package:fPix/com/longforus/fPix/widget/flutter_cache_manager.dart';
import 'package:fPix/com/longforus/fPix/SentryConfig.dart' as sentryConfig;
import 'package:flutter/material.dart';

void main() {
  runZoned<Future<void>>(() async {
    runApp(MyApp());
    // This captures errors reported by the Flutter framework.
    FlutterError.onError = (FlutterErrorDetails details) {
      if (sentryConfig.isInDebugMode) {
        // In development mode, simply print to console.
        FlutterError.dumpErrorToConsole(details);
      } else {
        // In production mode, report to the application zone to report to
        // Sentry.
        Zone.current.handleUncaughtError(details.exception, details.stack);
      }
    };
  }, onError: (error, stacktrace) {
    sentryConfig.reportError(error, stacktrace);
  });
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'fPix',
      theme: ThemeData(
        primaryColorBrightness: Brightness.dark,
        primaryColor: Color(0xff03A9F4),
        primaryColorDark: Color(0xff0288D1),
        primaryColorLight: Color(0xffB3E5FC),
        accentColor: Color(0xff8BC34A),
        dividerColor: Color(0xffBDBDBD),
        dialogBackgroundColor: Color.fromARGB(80, 255, 255, 255),
      ),
      home: MyHomePage(title: 'fPix'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int selectedPageIndex = 0;

  @override
  void initState() {
    CacheManager.showDebugLogs = true;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        bottomNavigationBar: new BottomNavigationBar(
          items: _getBottomNvBar(selectedPageIndex),
          currentIndex: selectedPageIndex,
          onTap: (index) {
            setState(() {
              if (index != selectedPageIndex) {
                selectedPageIndex = index;
              }
            });
          },
        ),
        body: _getPage(selectedPageIndex) // This trailing comma makes auto-formatting nicer
        // for build methods.
        );
  }

  Widget _getPage(int selectedPageIndex) {
    switch (selectedPageIndex) {
      case 0:
        return new ImagePage();
      case 1:
        return new VideoPage();
      case 2:
        return new FavoritePage();
      case 3:
        return new SettingsPage();
    }
  }

  List<BottomNavigationBarItem> _getBottomNvBar(int pageIndex) {
    return List.generate(4, (index) {
      return _genBNVItem(index, index == pageIndex);
    }).toList();
  }

  BottomNavigationBarItem _genBNVItem(int index, bool selected) {
    Color accentColor = Theme.of(context).accentColor;
    switch (index) {
      case 0:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.image,
              color: Colors.grey,
            ),
            activeIcon: new Icon(
              Icons.image,
              color: accentColor,
            ),
            title: new Text(
              "Image",
              style: TextStyle(color: selected ? accentColor : Colors.grey),
            ));
      case 1:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.video_label,
              color: Colors.grey,
            ),
            activeIcon: new Icon(
              Icons.video_label,
              color: accentColor,
            ),
            title: new Text(
              "Video",
              style: TextStyle(color: selected ? accentColor : Colors.grey),
            ));
      case 2:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.favorite,
              color: Colors.grey,
            ),
            activeIcon: new Icon(
              Icons.favorite,
              color: accentColor,
            ),
            title: new Text(
              "Favorite",
              style: TextStyle(color: selected ? accentColor : Colors.grey),
            ));
      case 3:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.settings,
              color: Colors.grey,
            ),
            activeIcon: new Icon(
              Icons.settings,
              color: accentColor,
            ),
            title: new Text(
              "Settings",
              style: TextStyle(color: selected ? accentColor : Colors.grey),
            ));
    }
  }
}
