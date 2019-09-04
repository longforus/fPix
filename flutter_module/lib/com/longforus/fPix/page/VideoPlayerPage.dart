import 'package:flutter/material.dart';
import 'package:chewie/chewie.dart';
import 'package:chewie/src/chewie_player.dart';
import 'package:flutter/cupertino.dart';
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

  VideoPlayerController _videoPlayerController;
  ChewieController _chewieController;

  @override
  void initState() {
    super.initState();
    _videoPlayerController = VideoPlayerController.network( widget.data['videos']['medium']['url']);
  }



  @override
  void dispose() {
   _videoPlayerController.dispose();
   _chewieController.dispose();
    super.dispose();
  }


  @override
  Widget build(BuildContext context) {
    _chewieController = ChewieController(
      videoPlayerController: _videoPlayerController,
      aspectRatio: 3 / 2,
      autoPlay: true,
      looping: true,
      // Try playing around with some of these other options:

      // showControls: false,
      materialProgressColors: ChewieProgressColors(
        playedColor:Theme.of(context).primaryColorDark,
        handleColor: Theme.of(context).primaryColorLight,
        backgroundColor: Colors.grey,
        bufferedColor: Theme.of(context).accentColor,
      ),
//       placeholder: Container(
//         color: Colors.grey,
//       ),
      // autoInitialize: true,
    );
    return new Scaffold(
      backgroundColor: Colors.black87,
      body: Chewie(
        controller: _chewieController,
      ),
    );
  }

}
