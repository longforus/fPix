import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/event/Events.dart';
import 'package:fPix/com/longforus/fPix/utils/Toast.dart';
import 'package:fPix/com/longforus/fPix/page/PhotoViewPage.dart';
import 'package:fPix/com/longforus/fPix/widget/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:fPix/com/longforus/fPix/utils/CacheUtil.dart';
/// @describe
/// @author  XQ Yang
/// @date 12/20/2018  3:46 PM

class ImageGridView extends StatelessWidget {
  ImageGridView({Key key, this.imageType}) : super(key: key);

  final String imageType;


  @override
  Widget build(BuildContext context) {
    var imageGridDelegate = new ImageGridDelegate(
      imageType: imageType,
    );

    return new RefreshIndicator(
      child: SafeArea(
        top: true,
        bottom: false,
        child: Builder(
          // This Builder is needed to provide a BuildContext that is "inside"
          // the NestedScrollView, so that sliverOverlapAbsorberHandleFor() can
          // find the NestedScrollView.
          builder: (BuildContext context) {
            return imageGridDelegate;
          },
        ),
      ),
      onRefresh: imageGridDelegate._onRefresh,
    );
  }
}

class ImageGridDelegate extends StatefulWidget {
  ImageGridDelegate({Key key, this.imageType}) : super(key: key);
  final String imageType;
  Future<void> Function() onRefresh;

  Future<void> _onRefresh() {
    return onRefresh();
  }

  @override
  State<StatefulWidget> createState() {
    var imageGridDelegateState = _ImageGridDelegateState(imageType);
    onRefresh = imageGridDelegateState._onRefresh;
    return imageGridDelegateState;
  }
}

class _ImageGridDelegateState extends State<ImageGridDelegate> {
  var httpClient = new HttpClient();
  final String imageType;

  int currentPageIndex = 1;
  final pageSize = 10;

  List dataList = new List();

  _ImageGridDelegateState(String type)
      : this.imageType = type.toLowerCase();

  @override
  void initState() {
    getImageData();
    super.initState();
  }

  void getImageData([Completer computer]) async {
    var url = BASE_URL;
    var httpClient = new HttpClient();
    bool success = false;
    url += "&category=$imageType";
    url += "&page=$currentPageIndex";
    url += "&per_page=$pageSize";
    List resultList;
    try {
      var request = await httpClient.getUrl(Uri.parse(url));
      var response = await request.close();
      if (response.statusCode == HttpStatus.ok) {
        var jsonStr = await response.transform(utf8.decoder).join();
        var data = json.decode(jsonStr);
        resultList = data['hits'];
        for (var value in resultList) {
          print(value['previewURL']);
          success = true;
        }
      } else {
        Toast.toast(context, 'Error getting status=${response.statusCode}');
      }
    } catch (exception) {
      Toast.toast(context, 'Failed getting');
    }
    computer?.complete();

    // If the widget was removed from the tree while the message was in flight,
    // we want to discard the reply rather than calling setState to update our
    // non-existent appearance.
    if (!mounted) return;

    if (success) {
      if (currentPageIndex == 1) {
        eventBus.fire(OnTopImageChangeEvent(resultList[0]));
      }
      setState(() {
        dataList.addAll(currentPageIndex == 1
            ? resultList.sublist(1, resultList.length - 1)
            : resultList);
      });
    }
  }

  Future<void> _onRefresh() {
    currentPageIndex = 1;
    dataList.clear();
    Completer computer = Completer<void>();
    getImageData(computer);
    return computer.future;
  }

  void _getMore() {
    currentPageIndex++;
    getImageData();
  }

  void _onCardTap(Map<String, dynamic> item) {
    Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) {
      return new PhotoViewPage(item);
    }));
  }

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      // The "controller" and "primary" members should be left
      // unset, so that the NestedScrollView can control this
      // inner scroll view.
      // If the "controller" property is set, then this scroll
      // view will not be associated with the NestedScrollView.
      // The PageStorageKey should be unique to this ScrollView;
      // it allows the list to remember its scroll position when
      // the tab view is not on the screen.
      key: PageStorageKey<String>(imageType),
      slivers: <Widget>[
        SliverPadding(
          padding: const EdgeInsets.all(2.0),
          // In this example, the inner scroll view has
          // fixed-height list items, hence the use of
          // SliverFixedExtentList. However, one could use any
          // sliver widget here, e.g. SliverList or SliverGrid.
          sliver: SliverGrid(
            // The items in this example are fixed to 48 pixels
            // high. This matches the Material Design spec for
            // ListTile widgets.
            delegate: SliverChildBuilderDelegate(
              (BuildContext context, int index) {
                // This builder is called for each child.
                // In this example, we just number each list item.
                if (index > currentPageIndex * pageSize - 2) {
                  _getMore();
                }
                return new GestureDetector(
                  child: new Card(
                      margin: const EdgeInsets.all(2.0),
                      elevation: 5,
                      child: Container(
                        padding: const EdgeInsets.all(2.0),
                        decoration: BoxDecoration(
                            shape: BoxShape.rectangle,
                            borderRadius: BorderRadius.circular(4),
                            image: DecorationImage(
                                image: dataList.isEmpty ||
                                        dataList.length - 1 < index
                                    ? AssetImage(
                                        'images/placeholder.png',
                                      )
                                    : CachedNetworkImageProvider(
                                        dataList[index]['previewURL']
                                    ),
                                fit: BoxFit.cover)),
                      )),
                  onTap: () {
                    if (dataList.isNotEmpty && dataList.length - 1 > index) {
                      _onCardTap(dataList[index]);
                    }
                  },
                );
              },
              // The childCount of the SliverChildBuilderDelegate
              // specifies how many children this inner list
              // has. In this example, each tab has a list of
              // exactly 30 items, but this is arbitrary.
              childCount: pageSize * currentPageIndex,
            ),
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
            ),
          ),
        ),
      ],
    );
  }
}
