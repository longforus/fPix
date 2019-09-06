import 'dart:developer';

import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/view/GridImageView.dart';
import 'package:fPix/com/longforus/fPix/widget/ImageTopBar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class ImagePage extends StatefulWidget {
  const ImagePage({
    Key key,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new _ImagePageState();
  }
}

class _ImagePageState extends State<ImagePage> with TickerProviderStateMixin {
  String searchContent;

  TabController _tabController;
  TabBarView tabBarView;
  ImageTopBar imageTopBar;




  @override
  void initState() {
    _tabController = TabController(length: typeList.length, vsync: this);
    tabBarView = TabBarView(
      // These are the contents of the tab views, below the tabs.
      children: typeList.map((String type) {
        return new ImageGridView(imageType: type);
      }).toList(),
      controller: _tabController,
    );
    imageTopBar = new ImageTopBar(
      isVideo: false,
    );
    super.initState();
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
                ImageGridView currentImageGridView = tabBarView.children[_tabController.index];
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
                ImageGridView currentImageGridView = tabBarView.children[_tabController.index];
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
}
