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

class _ImagePageState extends State<ImagePage>
    with SingleTickerProviderStateMixin {


  @override
  Widget build(BuildContext context) {
    var imageTopBar = new ImageTopBar(isVideo: false,);
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

