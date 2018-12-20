import 'dart:convert';
import 'dart:io';

import 'package:fPix/com/longforus/fPix/page/ImagePage.dart';
import 'package:flutter/material.dart';
import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/page/ImagePage.dart';

/// @describe
/// @author  XQ Yang
/// @date 12/20/2018  3:46 PM

class ImageGridView extends StatelessWidget {
  const ImageGridView({Key key, this.imageType, this.state}) : super(key: key);

  final String imageType;

  final ImageTopBarState state;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: true,
      bottom: false,
      child: Builder(
        // This Builder is needed to provide a BuildContext that is "inside"
        // the NestedScrollView, so that sliverOverlapAbsorberHandleFor() can
        // find the NestedScrollView.
        builder: (BuildContext context) {
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
//                      SliverOverlapInjector(
//                        // This is the flip side of the SliverOverlapAbsorber above.
//                        handle: NestedScrollView.sliverOverlapAbsorberHandleFor(
//                            context),
//                      ),
              SliverPadding(
                padding: const EdgeInsets.all(2.0),
                // In this example, the inner scroll view has
                // fixed-height list items, hence the use of
                // SliverFixedExtentList. However, one could use any
                // sliver widget here, e.g. SliverList or SliverGrid.
                sliver: new ImageGridDelegate(
                  imageType: imageType,
                  state: state,
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}

class ImageGridDelegate extends StatefulWidget {
  const ImageGridDelegate({Key key, this.imageType, this.state})
      : super(key: key);
  final String imageType;
  final ImageTopBarState state;

  @override
  State<StatefulWidget> createState() {
    return _ImageGridDelegateState(imageType, state);
  }
}

class _ImageGridDelegateState extends State<ImageGridDelegate> {
  var httpClient = new HttpClient();
  final ImageTopBarState state;
  final String imageType;

  int currentPageIndex = 1;
  final pageSize = 10;

  List dataList = new List();

  _ImageGridDelegateState(this.imageType, this.state);

  @override
  void initState() {
    getImageData(imageType.toLowerCase());
    super.initState();
  }

  void getImageData(String imageType) async {
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
        print('Error getting IP address:\nHttp status ${response.statusCode}');
      }
    } catch (exception) {
      print('Failed getting IP address');
    }

    // If the widget was removed from the tree while the message was in flight,
    // we want to discard the reply rather than calling setState to update our
    // non-existent appearance.
    if (!mounted) return;

    if (success) {
      if (currentPageIndex == 1) {
        state.onTopImageChanged(resultList[0]['webformatURL']);
      }
      setState(() {
        dataList.addAll(currentPageIndex == 1
            ? resultList.sublist(1, resultList.length - 1)
            : resultList);
        currentPageIndex++;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return SliverGrid(
      // The items in this example are fixed to 48 pixels
      // high. This matches the Material Design spec for
      // ListTile widgets.
      delegate: SliverChildBuilderDelegate(
        (BuildContext context, int index) {
          // This builder is called for each child.
          // In this example, we just number each list item.
          return new Card(
            margin: const EdgeInsets.all(2.0),
            child: Container(
              padding: const EdgeInsets.all(2.0),
              child:  dataList.isEmpty||dataList.length-1<index
                  ? Image.asset(
                'images/placeholder.png',
                fit: BoxFit.cover,
              )
                  : Image.network(
                dataList[index]['previewURL'],
                fit: BoxFit.cover,
              ),
            )
          );
        },
        // The childCount of the SliverChildBuilderDelegate
        // specifies how many children this inner list
        // has. In this example, each tab has a list of
        // exactly 30 items, but this is arbitrary.
        childCount: pageSize * currentPageIndex,
      ),
      gridDelegate:
          SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 2),
    );
  }
}
