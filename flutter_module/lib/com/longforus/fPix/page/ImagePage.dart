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
    "All",
    "Photo",
    "Illustration",
    "Vector"
  ];
  TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: typeList.length);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: typeList.length, // This is the number of tabs.
      child: NestedScrollView(
        headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
          // These are the slivers that show up in the "outer" scroll view.
          return <Widget>[
            new ImageTopBar()
          ];
        },
        body: TabBarView(
          // These are the contents of the tab views, below the tabs.
          children: typeList.map((String name) {
            return SafeArea(
              top: true,
              bottom: false,
              child: Builder(
                // This Builder is needed to provide a BuildContext that is "inside"
                // the NestedScrollView, so that sliverOverlapAbsorberHandleFor() can
                // find the NestedScrollView.
                builder: (BuildContext context) {
                  return CustomScrollView(
                    // The "controller" and "primary" members should be left
                    // unset, so that the NestedScrollView can control this
                    // inner scroll view.
                    // If the "controller" property is set, then this scroll
                    // view will not be associated with the NestedScrollView.
                    // The PageStorageKey should be unique to this ScrollView;
                    // it allows the list to remember its scroll position when
                    // the tab view is not on the screen.
                    key: PageStorageKey<String>(name),
                    slivers: <Widget>[
//                      SliverOverlapInjector(
//                        // This is the flip side of the SliverOverlapAbsorber above.
//                        handle: NestedScrollView.sliverOverlapAbsorberHandleFor(
//                            context),
//                      ),
                      SliverPadding(
                        padding: const EdgeInsets.all(2.0),
                        // In this example, the inner scroll view has
                        // fixed-height list items, hence the use of
                        // SliverFixedExtentList. However, one could use any
                        // sliver widget here, e.g. SliverList or SliverGrid.
                        sliver: SliverGrid(
                          // The items in this example are fixed to 48 pixels
                          // high. This matches the Material Design spec for
                          // ListTile widgets.
                          delegate: SliverChildBuilderDelegate(
                                (BuildContext context, int index) {
                              // This builder is called for each child.
                              // In this example, we just number each list item.
                              return Image.asset('images/${index%3+1}.png');
                            },
                            // The childCount of the SliverChildBuilderDelegate
                            // specifies how many children this inner list
                            // has. In this example, each tab has a list of
                            // exactly 30 items, but this is arbitrary.
                            childCount: 30,
                          ),
                          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: 2
                          ),
                        ),
                      ),
                    ],
                  );
                },
              ),
            );
          }).toList(),
        ),
      ),
    );
  }
}

class ImageTopBar extends StatefulWidget {
  const ImageTopBar({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new _ImageTopBarState();
  }
}

class _ImageTopBarState extends State<ImageTopBar> {
  @override
  Widget build(BuildContext context) {
    return SliverAppBar(
      pinned: true,
      floating: true,
      primary: true,
      expandedHeight: 250.0,
      flexibleSpace: FlexibleSpaceBar(
        background:  Image.asset('images/gift.jpg',fit: BoxFit.cover,),
      ),
      actions: <Widget>[
        new IconButton(icon: new Icon(Icons.star_border), onPressed: null)
      ],
      bottom: TabBar(
        tabs: _ImagePageState.typeList.map((String str) {
          return new Tab(
            text: str,
          );
        }).toList(),
      ),
    );
  }
}
