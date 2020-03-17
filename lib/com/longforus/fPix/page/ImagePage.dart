import 'dart:developer';

import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/view/GridImageView.dart';
import 'package:fPix/com/longforus/fPix/widget/ImageTopBar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class ImageAndVideoPage extends StatefulWidget {
  const ImageAndVideoPage({Key key, this.isVideo = false}) : super(key: key);
  final bool isVideo;

  @override
  State<StatefulWidget> createState() {
    return new _ImageAndVideoPageState(isVideo);
  }
}

class _ImageAndVideoPageState extends State<ImageAndVideoPage> with SingleTickerProviderStateMixin {
  String searchContent;
  final bool isVideo;
  TabController _tabController;
  TabBarView tabBarView;
  ImageTopBar imageTopBar;

  void _onTabChange() {
    debugPrint("onchangge  $searchContent");
    if (searchContent != null) {
      final ImageGridView currentImageGridView = tabBarView.children[_tabController.index];
      currentImageGridView.clearSearchStatus();
      setState(() {
        searchContent = null;
      });
    }
  }

  @override
  void initState() {
    _tabController = TabController(length: typeList.length, vsync: this);
    _tabController.addListener(_onTabChange);
    tabBarView = TabBarView(
      // These are the contents of the tab views, below the tabs.
      children: typeList.map((String type) {
        return new ImageGridView(
          imageType: type,
          isVideo: isVideo,
        );
      }).toList(),
      controller: _tabController,
    );
    imageTopBar = new ImageTopBar(
      isVideo: isVideo,
      tabController: _tabController,
    );
    super.initState();
  }

  @override
  void dispose() {
    _tabController.removeListener(_onTabChange);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: DefaultTabController(
        length: typeList.length, // This is the number of tabs.
        child: NestedScrollView(
            headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
              // These are the slivers that show up in the "outer" scroll view.
              return <Widget>[imageTopBar];
            },
            body: tabBarView),
      ),
      floatingActionButton: searchContent != null
          ? FloatingActionButton.extended(
              onPressed: () {
                final ImageGridView currentImageGridView = tabBarView.children[_tabController.index];
                currentImageGridView.clearSearchStatus();
                setState(() {
                  searchContent = null;
                });
              },
              tooltip: "Search",
              label: Row(
                children: <Widget>[
                  Text(
                    searchContent,
                    style: TextStyle(color: Colors.white, fontSize: 12),
                  ),
                  Icon(
                    Icons.close,
                    color: Colors.white,
                  )
                ],
              ),
              icon: Icon(
                Icons.search,
                color: Colors.white,
              ),
            )
          : FloatingActionButton(
              onPressed: () {
                final ImageGridView currentImageGridView = tabBarView.children[_tabController.index];
                currentImageGridView.showSearchDialog(context, (str) {
                  if (str != null && str.isNotEmpty) {
                    setState(() {
                      searchContent = str;
                    });
                  }
                });
              },
              tooltip: "Search",
              child: Icon(
                Icons.search,
                color: Colors.white,
              ),
            ),
    );
  }

  _ImageAndVideoPageState(this.isVideo);
}
