import 'package:fPix/com/longforus/fPix/view/GridImageView.dart';
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
  static const List<String> typeList = <String>[
    'Fashion',
    'Music',
    'Food',
    'Nature',
    'Backgrounds',
    'Science',
    'Education',
    'People',
    'Feelings',
    'Religion',
    'Health',
    'Places',
    'Animals',
    'Industry',
    'Computer',
    'Sports',
    'Transportation',
    'Travel',
    'Buildings',
    'Business'
  ];

  int currentPageIndex = 0;

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
              imageType: type,state: imageTopBar.state,
            );
          }).toList(),
        ),
      ),
    );
  }
}

class ImageTopBar extends StatefulWidget {
  ImageTopBar({Key key}) : super(key: key);
  ImageTopBarState state= new ImageTopBarState();

  @override
  State<StatefulWidget> createState() {
    return state;
  }
}

class ImageTopBarState extends State<ImageTopBar> {
  String topImgUrl = "";

  void onTopImageChanged(String url) {
    if(mounted){
      setState(() {
        topImgUrl = url;
      });
    }else{
      topImgUrl = url;
    }
  }

  @override
  Widget build(BuildContext context) {
    return SliverAppBar(
      pinned: true,
      floating: true,
      primary: true,
      expandedHeight: 250.0,
      flexibleSpace: FlexibleSpaceBar(
        background: topImgUrl.isEmpty
            ? Image.asset(
                'images/gift.jpg',
                fit: BoxFit.cover,
              )
            : Image.network(
                topImgUrl,
                fit: BoxFit.cover,
              ),
      ),
      actions: <Widget>[
        new IconButton(icon: new Icon(Icons.star_border), onPressed: null)
      ],
      bottom: TabBar(
        isScrollable: true,
        tabs: _ImagePageState.typeList.map((String str) {
          return new Tab(
            text: str,
          );
        }).toList(),
      ),
    );
  }
}
