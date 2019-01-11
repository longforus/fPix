import 'dart:async';

import 'package:fPix/com/longforus/fPix/event/Events.dart';
import 'package:fPix/com/longforus/fPix/view/GridImageView.dart';
import 'package:fPix/com/longforus/fPix/page/PhotoViewPage.dart';
import 'package:fPix/com/longforus/fPix/widget/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/utils/CacheUtil.dart';
class ImagePage extends StatefulWidget {
  const ImagePage({
    Key key,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new _ImagePageState();
  }
}

class _ImagePageState extends State<ImagePage>
    with SingleTickerProviderStateMixin {


  @override
  Widget build(BuildContext context) {
    var imageTopBar = new ImageTopBar();
    return DefaultTabController(
      length: typeList.length, // This is the number of tabs.
      child: NestedScrollView(
        headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
          // These are the slivers that show up in the "outer" scroll view.
          return <Widget>[imageTopBar];
        },
        body: TabBarView(
          // These are the contents of the tab views, below the tabs.
          children: typeList.map((String type) {
            return new ImageGridView(
              imageType: type
            );
          }).toList(),
        ),
      ),
    );
  }
}

class ImageTopBar extends StatefulWidget {
  ImageTopBar({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new ImageTopBarState();
  }
}

class ImageTopBarState extends State<ImageTopBar> {
  Map<String, dynamic> topImgUrl;
  StreamSubscription<OnTopImageChangeEvent> _streamSubscription;
  
  @override
  void initState() {
    _streamSubscription = eventBus.on<OnTopImageChangeEvent>().listen((event){
      _onTopImageChanged(event.item);
    });
    super.initState();
  }


  @override
  void dispose() {
    _streamSubscription?.cancel();
    super.dispose();
  }

  void _onTopImageChanged(Map<String, dynamic> url) {
    if (mounted) {
      setState(() {
        topImgUrl = url;
      });
    } else {
      topImgUrl = url;
    }
  }

  void _go2PhotoPage() {
    Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) {
      return new PhotoViewPage( topImgUrl,);
    }));
  }

  @override
  Widget build(BuildContext context) {
    return SliverAppBar(
      pinned: true,
      floating: true,
      primary: true,
      expandedHeight: 250.0,
      flexibleSpace: FlexibleSpaceBar(
        background: topImgUrl == null
            ? Image.asset(
                'images/placeholder.png',
                fit: BoxFit.cover,
              )
            : GestureDetector(
                child: CachedNetworkImage(
                  imageUrl: topImgUrl['webformatURL'],
                  cacheKey: getCacheKey(topImgUrl, 'webformatURL'),
                  fit: BoxFit.cover,
                ),
                onTap: _go2PhotoPage,
              ),
      ),
      actions: <Widget>[
        topImgUrl != null
            ? new Container(
                padding: const EdgeInsets.all(8.0),
                child: new Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    new Icon(
                      Icons.thumb_up,
                      size: 15,
                      color: Colors.red,
                    ),
                    new Text(' ${topImgUrl['likes']}')
                  ],
                ),
              )
            : new Icon(Icons.thumb_up)
      ],
      bottom: TabBar(
        isScrollable: true,
        tabs: typeList.map((String str) {
          return new Tab(
            text: str,
          );
        }).toList(),
      ),
    );
  }
}
