/// @describe
/// @author  XQ Yang
/// @date 12/26/2018  2:38 PM

String getCacheKey(Map<String, dynamic> imageData, String imgKey) {
  String imgUrl = imageData[imgKey];
  String size =
      imgUrl.substring(imgUrl.lastIndexOf('_'), imgUrl.lastIndexOf('.'));
  return "${imageData['id']}$size";
}
