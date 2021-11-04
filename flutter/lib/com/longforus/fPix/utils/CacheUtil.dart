/// @describe
/// @author  XQ Yang
/// @date 12/26/2018  2:38 PM

String getCacheKey(Map<String, dynamic> imageData, String imgKey) {
  String? imgUrl = imageData[imgKey];
  if(imgUrl==null||imgUrl.isEmpty){
      return "";
  }
  String size = imgUrl.substring(imgUrl.lastIndexOf('_'), imgUrl.lastIndexOf('.'));
  return "${imageData['id']}$size";
}


String getVideoImageUrl(Map<String, dynamic> data, {String size = "640x360"}) {
    return "https://i.vimeocdn.com/video/${data['picture_id']}_$size.jpg";
}
