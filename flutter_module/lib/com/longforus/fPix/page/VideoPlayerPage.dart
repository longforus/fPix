import 'package:flutter/material.dart';
import 'package:custom_chewie/custom_chewie.dart';
import 'package:video_player/video_player.dart';

///
///
///@author  XQ Yang
///@date 1/23/2019  10:50 AM
///
class VideoPlayerPage extends StatefulWidget {
  final Map<String, dynamic> data;

  VideoPlayerPage(this.data);

  @override
  State<StatefulWidget> createState() => _VideoPlayerPageState();
}

class _VideoPlayerPageState extends State<VideoPlayerPage> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      backgroundColor: Colors.black87,
      body: new Chewie(
    new VideoPlayerController.network(
    widget.data['videos']['medium']['url']),
    aspectRatio: 3 / 2,
    autoPlay: true,
    looping: true,
    )
    );
  }
}
