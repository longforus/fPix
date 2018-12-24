import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';

/// @describe
/// @author  XQ Yang
/// @date 12/24/2018  2:09 PM
class PhotoViewPage extends StatefulWidget {
  final Map<String, dynamic> imageData;

  PhotoViewPage(this.imageData);

  @override
  State<StatefulWidget> createState() => _PhotoViewPageState();
}

class _PhotoViewPageState extends State<PhotoViewPage> {
  bool downloaded = false;
  bool favorited = false;

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
        actions: <Widget>[
          Container(
            child: new Icon(
              Icons.file_download,
              color: downloaded ? accentColor : Colors.grey[600],
            ),
            margin: const EdgeInsets.symmetric(horizontal: 8.0),
          ),
          Container(
            child: new Icon(
              favorited ? Icons.favorite : Icons.favorite_border,
              color: favorited ? accentColor : Colors.grey[600],
            ),
            margin: const EdgeInsets.symmetric(horizontal: 8.0),
          )
        ],
      ),
      body: Stack(
        alignment: Alignment.bottomLeft,
        children: <Widget>[
          PhotoView(
            imageProvider: NetworkImage(widget.imageData['largeImageURL']),
            minScale: PhotoViewComputedScale.contained * 0.8,
            heroTag: widget.imageData['largeImageURL'],
          ),
          Container(
            padding: EdgeInsets.all(12.0),
            width: double.infinity,
            decoration: BoxDecoration(color: Colors.black26),
            child: Text(
              widget.imageData['tags'],
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
        context.paintChild(i,
            transform: new Matrix4.translationValues(x, y, 0.0));
        x = w + margin.left;
      } else {
        x = margin.left;
        y += context.getChildSize(i).height + margin.top + margin.bottom;
        context.paintChild(i,
            transform: new Matrix4.translationValues(x, y, 0.0));
        x += context.getChildSize(i).width + margin.left + margin.right;
      }
    }
  }

  @override
  bool shouldRepaint(FlowDelegate oldDelegate) {
    return oldDelegate != this;
  }
}
