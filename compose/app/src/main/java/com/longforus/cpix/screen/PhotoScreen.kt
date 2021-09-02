package com.longforus.cpix.screen

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.github.chrisbanes.photoview.PhotoView
import com.longforus.cpix.bean.Item
import com.longforus.cpix.ui.theme.Purple500

@Composable
fun PhotoScreen(img: Item?,navHostController: NavHostController){
    Scaffold(
        backgroundColor = Color.Black
    ) {
        Box(
            contentAlignment = Alignment.TopCenter, modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            val imageDrawable by loadNetworkImage(img?.largeImageURL, LocalContext.current)
            ImageContent(imageDrawable)
            TopAppBar(
                backgroundColor = Color(0x22000000),
                modifier = Modifier.padding(top = 20.dp),
                elevation = 1.dp
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        navHostController.navigateUp()
                    },
                    tint = Color.White
                )
            }
        }

    }
}


@Composable
private fun ImageContent(drawable: Drawable?) {
    if (drawable == null) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Purple500)
        }
    } else {
        AndroidView(
            factory = {
                PhotoView(it).apply {
                    load(drawable)
                }
            }, modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        )
    }

}


@Composable
fun loadNetworkImage(
    url: String?,
    context: Context
): State<Drawable?> {

    // Creates a State<T> with Result.Loading as initial value
    // If either `url` or `imageRepository` changes, the running producer
    // will cancel and will be re-launched with the new keys.
    return produceState<Drawable?>(initialValue = null) {
        value = context.imageLoader.execute(ImageRequest.Builder(context).data(url).build()).drawable
    }
}