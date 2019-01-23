import 'package:fPix/com/longforus/fPix/Const.dart';
import 'package:fPix/com/longforus/fPix/view/GridImageView.dart';
import 'package:fPix/com/longforus/fPix/widget/ImageTopBar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
class VideoPage extends StatefulWidget {
  const VideoPage({
    Key key,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new _VideoPageState();
  }
}

class _VideoPageState extends State<VideoPage>
    with SingleTickerProviderStateMixin {


  @override
  Widget build(BuildContext context) {
    var imageTopBar = new ImageTopBar(isVideo: true,);
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
              imageType: type,isVideo: true,
            );
          }).toList(),
        ),
      ),
    );
  }
}
