import 'dart:io';


import 'package:fPix/com/longforus/fPix/bean/favorite_bean.dart';
import 'package:fPix/com/longforus/fPix/db/OB.dart';
import 'package:fPix/com/longforus/fPix/utils/FileManager.dart';
import 'package:fPix/com/longforus/fPix/utils/Toast.dart';
import 'package:fPix/com/longforus/fPix/widget/cached_network_image.dart';
import 'package:fPix/com/longforus/fPix/widget/flutter_cache_manager.dart';
import 'package:fPix/objectbox.g.dart';
import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:fPix/com/longforus/fPix/utils/CacheUtil.dart';

/// @describe
/// @author  XQ Yang
/// @date 12/24/2018  2:09 PM
class PhotoViewPage extends StatefulWidget {
   Map<String, dynamic> imageData;

  PhotoViewPage(this.imageData, {this.onFavoriteChanged});

  ValueChanged<bool> onFavoriteChanged;

  @override
  State<StatefulWidget> createState() => _PhotoViewPageState();
}

class _PhotoViewPageState extends State<PhotoViewPage> {
  bool downloaded = false;
  bool favorited = false;
  Box<FavoriteBean> _box;
  @override
  void initState() {
    if (widget.imageData != null) {
      OB.getInstance().then((value){
         _box = value.store.box();
         if (mounted) {
           setState(() {
             favorited = _box.contains(widget.imageData['id']);
           });
         }
      });
    } else {
      setState(() {
        favorited = true;
      });
    }
    super.initState();
  }

  ///
  /// 显示有点问题,暂时不用吧
  ///
  List<Widget> _getTagButton() {
    var accentColor = Theme.of(context).accentColor;
    List<String> tags = widget.imageData['tags'].toString().split(',');
    return List.generate(tags.length, (index) {
      return Container(
//        alignment: Alignment.center,
        padding: EdgeInsets.symmetric(vertical: 8.0),
        decoration: BoxDecoration(color: Colors.black26),
        child: Text(
          tags[index],
          style: new TextStyle(
            color: accentColor,
            fontSize: 16.0,
            fontWeight: FontWeight.bold,
          ),
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    var accentColor = Theme.of(context).accentColor;
    return Scaffold(
      backgroundColor: Colors.black87,
      appBar: new AppBar(
        backgroundColor: Colors.transparent,
        brightness: Brightness.dark,
        iconTheme: new IconThemeData(color: Colors.grey[600]),
        actions: <Widget>[
          new IconButton(
            icon: new Icon(
              Icons.file_download,
              color: downloaded ? accentColor : Colors.grey[600],
            ),
            onPressed: () {
              _onDownload(context);
            },
          ),
          IconButton(
              icon: new Icon(
                Icons.open_in_browser,
                color: Colors.grey[600],
              ),
              onPressed: _onOpenInBrowser),
          IconButton(
              icon: new Icon(
                favorited ? Icons.favorite : Icons.favorite_border,
                color: favorited ? accentColor : Colors.grey[600],
              ),
              onPressed: widget.imageData != null ? _onFavorite : null)
        ],
      ),
      body: Stack(
        alignment: Alignment.bottomLeft,
        children: <Widget>[
          PhotoView(
            imageProvider: CachedNetworkImageProvider(widget.imageData['largeImageURL'],
                cacheKey: getCacheKey(widget.imageData, 'largeImageURL')),
            minScale: PhotoViewComputedScale.contained * 0.8,
            // heroAttributes: PhotoViewHeroAttributes(tag:  widget.imageData['largeImageURL']),
          ),
          Container(
            padding: EdgeInsets.all(12.0),
            width: double.infinity,
            decoration: BoxDecoration(color: Colors.black26),
            child: Text(
              widget.imageData['tags'] == null ? "" : widget.imageData['tags'],
              style: TextStyle(
                color: accentColor,
                fontWeight: FontWeight.bold,
              ),
              softWrap: true,
            ),
          )
//          Container(
//            padding: EdgeInsets.all(8.0),
//            height: 50,
//            child: Flow(
//              delegate: TagFlowDelegate(
//                  margin: const EdgeInsets.symmetric(vertical: 2.0)),
//              children: _getTagButton(),
//            ),
//          )
        ],
      ),
    );
  }

  void _onFavorite() {
    if (favorited) {
      setState(() {
        favorited =  _box.remove(widget.imageData['id']);
      });
    } else {
      setState(() {
        favorited = _box.put(FavoriteBean.formMap(widget.imageData))!=0;
      });
    }
    if (widget.onFavoriteChanged != null) {
      widget.onFavoriteChanged(true);
    }
  }

  void _onOpenInBrowser() async {
    var url = widget.imageData['pageURL'];
    if (await canLaunch(url)) {
      await launch(url);
    } else {
      throw 'Could not launch $url';
    }
  }

  void _onDownload(BuildContext context) async {
    CacheManager.getInstance().then((manager) {
      manager
          .getFile(widget.imageData['largeImageURL'], cacheKey: getCacheKey(widget.imageData, 'largeImageURL'))
          .then((file) {
        print('${file.path}');
        file.exists().then((b) {
          if (b) {
            try {
              FileManager.get(context).then((manager) async {
                if (manager != null) {
                  File result = await manager.save2SdCard(file);
                  Toast.toast(context, 'save to ${result.path}');
                } else {
                  Toast.toast(context, '存储权限未获取');
                }
              });
            } catch (e) {
              Toast.toast(context, 'Save Fail');
            }
          } else {
            Toast.toast(context, 'Download Fail');
          }
        });
      });
    });
  }
}

class TagFlowDelegate extends FlowDelegate {
  EdgeInsets margin = EdgeInsets.zero;

  TagFlowDelegate({this.margin});

  @override
  void paintChildren(FlowPaintingContext context) {
    var x = margin.left;
    var y = margin.top;
    for (int i = 0; i < context.childCount; i++) {
      var w = context.getChildSize(i).width + x + margin.right;
      if (w < context.size.width) {
        context.paintChild(i, transform: new Matrix4.translationValues(x, y, 0.0));
        x = w + margin.left;
      } else {
        x = margin.left;
        y += context.getChildSize(i).height + margin.top + margin.bottom;
        context.paintChild(i, transform: new Matrix4.translationValues(x, y, 0.0));
        x += context.getChildSize(i).width + margin.left + margin.right;
      }
    }
  }

  @override
  bool shouldRepaint(FlowDelegate oldDelegate) {
    return oldDelegate != this;
  }
}
