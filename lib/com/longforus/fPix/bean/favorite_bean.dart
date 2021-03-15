import 'package:fPix/com/longforus/fPix/bean/cache_object.dart';
import 'package:objectbox/objectbox.dart';

@Entity()
class FavoriteBean{

    @Id(assignable: true)
    int id;
    String tags;
    String pageURL;
    String largeImageURL;
    final cache = ToOne<CacheObject>();

    FavoriteBean();

    FavoriteBean.formMap(Map<String, dynamic> imageData){
        id = imageData['id'];
        tags = imageData['tags'];
        pageURL = imageData['pageURL'];
        largeImageURL = imageData['largeImageURL'];
    }

    Map<String, dynamic> toMap(){
        return {
            'id':id,
            'tags':tags,
            'pageURL':pageURL,
            'largeImageURL':largeImageURL,
        };
    }

    String getCacheKey() {
        String imgUrl = largeImageURL;
        if(imgUrl==null||imgUrl.isEmpty){
            return "";
        }
        String size = imgUrl.substring(imgUrl.lastIndexOf('_'), imgUrl.lastIndexOf('.'));
        return "${id}$size";
    }
}