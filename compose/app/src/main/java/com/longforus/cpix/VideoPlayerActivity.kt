package com.longforus.cpix

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.longforus.cpix.bean.Item
import com.longforus.cpix.ui.theme.CPixTheme
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.util.StatusBarUtil
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class VideoPlayerActivity : AppCompatActivity() {
    private var videoPlayer: StandardGSYVideoPlayer? = null
    private var orientationUtils: OrientationUtils? = null
    private val bean by lazy { intent.getParcelableExtra<Item>("bean") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            CPixTheme {
                Scaffold(
                    content = {
                        AndroidView(
                            factory = {
                                NormalGSYVideoPlayer(it).apply {
                                    videoPlayer = this
                                    initPlayer()
                                }
                            }, modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .padding(top = 33.dp)
                        )
                    },
                    backgroundColor = Color.Black
                )
            }
        }
    }

    private fun initPlayer() {
        videoPlayer?.apply {
            setUp(bean?.videos?.medium?.url, true, bean?.tags)
            //增加封面
            val imageView = ImageView(this@VideoPlayerActivity)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.load(bean?.coverImageUrl) {
                crossfade(true)
            }
            thumbImageView = imageView
            //增加title
            titleTextView.visibility = View.VISIBLE
            //设置返回键
            backButton.visibility = View.VISIBLE
            //设置旋转
            orientationUtils = OrientationUtils(this@VideoPlayerActivity, videoPlayer)
            //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
            fullscreenButton
                ?.setOnClickListener { orientationUtils?.resolveByClick() }
            //是否可以滑动调整
            setIsTouchWiget(true)
            //设置返回按键功能
            backButton.setOnClickListener { onBackPressed() }
            startPlayLogic()
            this.setDialogProgressColor(Purple500.toArgb(),Color.White.toArgb())
        }

    }


    override fun onPause() {
        super.onPause()
        videoPlayer?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        orientationUtils?.releaseListener()
    }

    override fun onBackPressed() {
        //先返回正常状态
        if (orientationUtils?.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer?.fullscreenButton?.performClick()
            return
        }
        //释放所有
        videoPlayer?.setVideoAllCallBack(null)
        super.onBackPressed()
    }
}