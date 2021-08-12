package com.longforus.cpix

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.longforus.cpix.bean.Img
import com.longforus.cpix.util.StatusBarUtil

class PhotoActivity : AppCompatActivity() {

    private val img by lazy { intent.getParcelableExtra<Img>("bean") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            Scaffold(
                backgroundColor = Color.Black
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter, modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = {
                            com.github.chrisbanes.photoview.PhotoView(it).apply {
                                load(img?.largeImageURL)
                            }
                        }, modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    )
                    TopAppBar(
                        backgroundColor = Color(0x22000000),
                        modifier = Modifier.padding(top = 20.dp),
                        elevation = 1.dp
                    ) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                finish()
                            },
                            tint = Color.White
                        )
                    }
                }

            }
        }
    }
}