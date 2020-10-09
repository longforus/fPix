import 'dart:async';

import 'package:fPix/com/longforus/fPix/page/FavoritePage.dart';
import 'package:fPix/com/longforus/fPix/page/ImagePage.dart';
import 'package:fPix/com/longforus/fPix/page/SettingsPage.dart';
import 'package:fPix/com/longforus/fPix/widget/flutter_cache_manager.dart';
import 'package:fPix/com/longforus/fPix/SentryConfig.dart' as sentryConfig;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  runZoned<Future<void>>(() async {
    runApp(MultiProvider(
      providers: [
          ChangeNotifierProvider(
          create: (_) => CurrentPageModel(),
        )
      ],
      child: MyApp(),
    ));
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
    SharedPreferences.setMockInitialValues({});
    CacheManager.showDebugLogs = true;
    Color accentColor = Color(0xff8BC34A);
    return MaterialApp(
      title: 'fPix',
      theme: ThemeData(
        primaryColorBrightness: Brightness.dark,
        primaryColor: Color(0xff03A9F4),
        primaryColorDark: Color(0xff0288D1),
        primaryColorLight: Color(0xffB3E5FC),
        accentColor: accentColor,
        dividerColor: Color(0xffBDBDBD),
        dialogBackgroundColor: Color.fromARGB(80, 255, 255, 255),
      ),
      home: Consumer<CurrentPageModel>(
        builder: (context, selectedPageIndex, child) => Scaffold(
            bottomNavigationBar: new BottomNavigationBar(
              items: _getBottomNvBar(context),
              currentIndex: selectedPageIndex.value,
                selectedLabelStyle: TextStyle(color: accentColor,fontSize: 15),
                unselectedLabelStyle: TextStyle(color: Colors.grey,fontSize: 10),
                selectedIconTheme:IconThemeData(color: accentColor,size: 36.0) ,
                unselectedIconTheme: IconThemeData(color: Colors.grey,size: 24.0),
              onTap: (index) {
                selectedPageIndex.change(index);
              },
            ),
            body: _getPage(selectedPageIndex.value) // This trailing comma makes auto-formatting nicer
            // for build methods.
            ),
      ),
    );
  }

  Widget _getPage(int selectedPageIndex) {
    switch (selectedPageIndex) {
      case 0:
        return new ImageAndVideoPage(
          key: Key("image"),
        );
      case 1:
        return new ImageAndVideoPage(
          key: Key("video"),
          isVideo: true,
        );
      case 2:
        return new FavoritePage();
      case 3:
        return new SettingsPage();
    }
  }

  List<BottomNavigationBarItem> _getBottomNvBar(context) {
    return List.generate(4, (index) {
      return _genBNVItem(context,index);
    }).toList();
  }

  BottomNavigationBarItem _genBNVItem(context,int index) {
    switch (index) {
      case 0:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.image,
            ),
            title: new Text(
              "Image",
            ));
      case 1:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.video_label,
            ),
            title: new Text(
              "Video",
            ));
      case 2:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.favorite,
            ),
            title: new Text(
              "Favorite",
            ));
      case 3:
        return new BottomNavigationBarItem(
            icon: new Icon(
              Icons.settings,
            ),
            title: new Text(
              "Settings",
            ));
    }
  }
}

class CurrentPageModel with ChangeNotifier {
  int _currentPageIndex = 0;

  int get value => _currentPageIndex;

  void change(int index) {
    if (index >= 0 && index <= 3 && _currentPageIndex != index) {
      _currentPageIndex = index;
      notifyListeners();
    }
  }
}
