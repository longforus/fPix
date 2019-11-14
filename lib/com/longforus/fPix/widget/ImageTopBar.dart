import 'dart:async';

import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/event/Events.dart';
import 'package:fPix/com/longforus/fPix/page/PhotoViewPage.dart';
import 'package:fPix/com/longforus/fPix/page/VideoPlayerPage.dart';
import 'package:fPix/com/longforus/fPix/utils/CacheUtil.dart';
import 'package:fPix/com/longforus/fPix/widget/cached_network_image.dart';
import 'package:flutter/material.dart';

class ImageTopBar extends StatefulWidget {
    ImageTopBar({Key key, this.isVideo,this.tabController}) : super(key: key);
    final bool isVideo;
    final TabController tabController;
    @override
    State<StatefulWidget> createState() {
        return new ImageTopBarState();
    }
}

class ImageTopBarState extends State<ImageTopBar> {
    Map<String, dynamic> topData;
    StreamSubscription<OnTopImageChangeEvent> _streamSubscription;

    @override
    void initState() {
        _streamSubscription = eventBus.on<OnTopImageChangeEvent>().listen((event) {
            _onTopImageChanged(event.item);
        });
        super.initState();
    }

    @override
    void dispose() {
        _streamSubscription?.cancel();
        super.dispose();
    }

    void _onTopImageChanged(Map<String, dynamic> data) {
        if (mounted) {
            setState(() {
                topData = data;
            });
        } else {
            topData = data;
        }
    }

    void _go2PhotoPage() {
        Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) {
            return widget.isVideo
                ? new VideoPlayerPage(topData)
                : new PhotoViewPage(
                topData,
            );
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
                background: topData == null
                    ? Image.asset(
                    'images/placeholder.png',
                    fit: BoxFit.cover,
                )
                    : GestureDetector(
                    child: CachedNetworkImage(
                        imageUrl: widget.isVideo ? getVideoImageUrl(topData) : topData['webformatURL'],
                        cacheKey: widget.isVideo ? getVideoImageUrl(topData) : getCacheKey(topData, 'webformatURL'),
                        fit: BoxFit.cover,
                    ),
                    onTap: _go2PhotoPage,
                ),
            ),
            actions: <Widget>[
                new Container(
                    padding: const EdgeInsets.all(8.0),
                    child: topData != null
                        ? new Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: <Widget>[
                            new Icon(
                                Icons.thumb_up,
                                size: 15,
                                color: Theme.of(context).accentColor,
                            ),
                            new Text(' ${topData['likes']}')
                        ],
                    )
                        : new Icon(
                        Icons.thumb_up,
                        size: 15,
                    ),
                )
            ],
            bottom: TabBar(
                controller: widget.tabController,
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
