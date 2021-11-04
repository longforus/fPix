import 'package:fPix/com/longforus/fPix/bean/favorite_bean.dart';
import 'package:fPix/com/longforus/fPix/db/OB.dart';
import 'package:fPix/com/longforus/fPix/page/PhotoViewPage.dart';
import 'package:fPix/com/longforus/fPix/widget/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class FavoritePage extends StatefulWidget {
  const FavoritePage({
    Key? key,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new _FavoritePageState();
  }
}

class _FavoritePageState extends State<FavoritePage> {
  List<FavoriteBean>? imgUrlList;

  @override
  void initState() {
    super.initState();
    var box = OB.getInstance()!.store!.box<FavoriteBean>();
    setState(() {
      imgUrlList = box.getAll();
      print(imgUrlList);
    });
  }

  List<Widget> _getFavoriteImageList() {
    return List.generate(imgUrlList!.length, (index) {
            return new GestureDetector(
              child: new Card(
                  margin: const EdgeInsets.all(2.0),
                  elevation: 5,
                  child: Hero(tag: imgUrlList![index].largeImageURL!, child: Container(
                    padding: const EdgeInsets.all(2.0),
                    decoration: BoxDecoration(
                        shape: BoxShape.rectangle,
                        borderRadius: BorderRadius.circular(4),
                        image: DecorationImage(
                            image: (imgUrlList!.isEmpty ||
                                imgUrlList!.length - 1 < index
                                ? AssetImage(
                              'images/placeholder.png',
                            )
                                : CachedNetworkImageProvider(
                                imgUrlList![index].largeImageURL!,cacheKey: imgUrlList![index].getCacheKey())) as ImageProvider<Object>,
                            fit: BoxFit.cover)),
                  ))),
              onTap: () {
                onImageClick(index);
              },
            );
          });
  }

  void onImageClick(int index){
    if (imgUrlList!.isNotEmpty && imgUrlList!.length - 1 >= index) {
      Navigator.push(context,
          MaterialPageRoute(builder: (BuildContext context) {
        return new PhotoViewPage(
          imgUrlList![index].toMap(),onFavoriteChanged: (changed){
          setState(() {
            imgUrlList!.removeAt(index);
          });
        },
        );
      }));
    }
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: AppBar(
        title: new Text('Favorite'),
      ),
      body: imgUrlList == null || imgUrlList!.length == 0
          ? new Center(
        child: new Text('Favorite List is Empty!'),
      )
          : GridView.count(
        crossAxisCount: 2,
        children: _getFavoriteImageList(),
      ),
    );
  }
}
