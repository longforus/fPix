import 'package:objectbox/objectbox.dart';

@Entity()
class FavoriteBean{

    @Id(assignable: true)
    int id;
    String tags;
    String pageURL;
    String largeImageURL;

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
}