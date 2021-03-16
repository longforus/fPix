import 'dart:async';

import 'package:fPix/com/longforus/fPix/db/OB.dart';
import 'package:fPix/com/longforus/fPix/http/dio_manager.dart';
import 'package:fPix/com/longforus/fPix/page/FavoritePage.dart';
import 'package:fPix/com/longforus/fPix/page/ImagePage.dart';
import 'package:fPix/com/longforus/fPix/page/SettingsPage.dart';
import 'package:fPix/com/longforus/fPix/widget/flutter_cache_manager.dart';
import 'package:fPix/com/longforus/fPix/SentryConfig.dart' as sentryConfig;
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
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
      // SharedPreferences.setMockInitialValues({});
    return GetMaterialApp(
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

/*class MyApp extends StatefulWidget {
  // This widget is the root of your application.
  @override
  State<StatefulWidget> createState() => MyAppState();
}


class MyAppState extends State<MyApp>{


    @override
  void initState() {
    super.initState();
    FlutterBoost.singleton.registerPageBuilders({
        'homePage': (pageName, params, _) => MyHomePage(title: params["pageTitle"],),
    });
  }

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
        home: Container(),
        builder: FlutterBoost.init(postPush: _onRoutePushed),
    );
  }

    void _onRoutePushed(
        String pageName, String uniqueId, Map params, Route route, Future _) {
        Logger.log("_onRoutePushed, name $pageName");
    }

}*/

class MyHomePage extends StatefulWidget {

  final String title;
  MyHomePage({Key key, this.title}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return HomePageState();
  }
}


class HomePageState extends State<MyHomePage>{

  @override
  void initState() {
    super.initState();
    CacheManager.showDebugLogs = !kReleaseMode;
    OB.getInstance();
    DioManager.baseUrl = 'https://pixabay.com/api';
    DioManager.reqCons = {
      'key':'11042541-60a032dcf49543f53d415848c'
    };
  }


  @override
  void dispose(){
    OB.getInstance().dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // 使用Get.put()实例化你的类，使其对当下的所有子路由可用。
    final IndexController ic = Get.put(IndexController());

    return Scaffold(
        bottomNavigationBar: Obx(()=>BottomNavigationBar(
          items: _getBottomNvBar(context,ic.index),
          currentIndex: ic.index,
          onTap: (index) {
            ic.index = index;
          },
        )),
        body:Obx(()=> _getPage(ic.index)) // This trailing comma makes auto-formatting nicer
      // for build methods.
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

  List<BottomNavigationBarItem> _getBottomNvBar(BuildContext context,int pageIndex) {
    return List.generate(4, (index) {
      return _genBNVItem(context,index, index == pageIndex);
    }).toList();
  }

  BottomNavigationBarItem _genBNVItem(BuildContext context,int index, bool selected) {
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


class IndexController extends GetxController {

  final _obj = 0.obs;
  set index(value) => _obj.value = value;
  get index => _obj.value;
}

