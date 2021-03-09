
import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/page/PhotoViewPage.dart';
import 'package:fPix/com/longforus/fPix/page/VideoPlayerPage.dart';
import 'package:fPix/com/longforus/fPix/utils/CacheUtil.dart';
import 'package:fPix/com/longforus/fPix/widget/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

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
    final TopImageController tc = Get.put(TopImageController());


    void _go2PhotoPage() {
       Get.to(()=>widget.isVideo
           ? new VideoPlayerPage(tc.topData)
           : new PhotoViewPage(tc.topData,
       ));
    }

    @override
    Widget build(BuildContext context) {
        return SliverAppBar(
            pinned: true,
            floating: true,
            primary: true,
            expandedHeight: 250.0,
            flexibleSpace: FlexibleSpaceBar(
                background: Obx(()=>tc.topData.isEmpty
                    ? Image.asset(
                    'images/placeholder.png',
                    fit: BoxFit.cover,
                )
                    : GestureDetector(
                    child: CachedNetworkImage(
                        imageUrl: widget.isVideo ? getVideoImageUrl(tc.topData) : tc.topData['webformatURL'],
                        cacheKey: widget.isVideo ? getVideoImageUrl(tc.topData) : getCacheKey(tc.topData, 'webformatURL'),
                        fit: BoxFit.cover,
                    ),
                    onTap: _go2PhotoPage,
                ),
                )),
            actions: <Widget>[
                new Container(
                    padding: const EdgeInsets.all(8.0),
                    child: Obx(()=>tc.topData.isNotEmpty
                        ? new Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: <Widget>[
                            new Icon(
                                Icons.thumb_up,
                                size: 15,
                                color: Theme.of(context).accentColor,
                            ),
                            new Text('${tc.topData['likes']}')
                        ],
                    )
                        : new Icon(
                        Icons.thumb_up,
                        size: 15,
                    ),)
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



class TopImageController extends GetxController {

  final _topData = RxMap<String,dynamic>().obs;
  set topData(value) => _topData.value.assignAll(value);
  Map<String,dynamic> get topData => _topData.value;
}