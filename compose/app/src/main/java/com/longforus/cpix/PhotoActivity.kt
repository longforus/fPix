package com.longforus.cpix

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.github.chrisbanes.photoview.PhotoView
import com.longforus.cpix.bean.Item
import com.longforus.cpix.bean.OB
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.util.StatusBarUtil
import com.longforus.cpix.viewmodel.PhotoViewModel
import kotlinx.coroutines.launch

class PhotoActivity : AppCompatActivity() {

    val TAG = "PhotoActivity"
    private val imgBean by lazy { intent.getParcelableExtra<Item>("bean") }
    private val viewModel by viewModels<PhotoViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PhotoViewModel(imgBean!!) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            imgBean?.let {
                val favorited by viewModel.favorited.collectAsState()
                val downloaded by viewModel.downloaded.collectAsState()
                PhotoContent(it, favorited, downloaded)
            }
        }
    }

    @Composable
    private fun PhotoContent(img: Item, contains: Boolean, downloaded: Boolean) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        Scaffold(
            backgroundColor = Color.Black,
            scaffoldState = scaffoldState
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                val imageDrawable by loadNetworkImage(img.largeImageURL, LocalContext.current)
                ImageContent(imageDrawable)
                TopAppBar(
                    backgroundColor = Color(0x22000000),
                    modifier = Modifier
                        .padding(top = 34.dp)
                        .align(Alignment.TopCenter),
                    elevation = 0.dp,
                    navigationIcon = {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    finish()
                                }
                                .padding(start = 15.dp),
                            tint = Color.Gray
                        )
                    },
                    actions = {
                        // Creates a CoroutineScope bound to the lifecycle
                        val scope = rememberCoroutineScope()
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                if (downloaded) {
                                    viewModel.openDownLoadedImage(img.downloadFile(this@PhotoActivity))
                                } else {
                                    scope.launch {
                                        if (viewModel.saveImage(imageDrawable, img.downloadFile(this@PhotoActivity))) {
                                            val showSnackbar = scaffoldState.snackbarHostState.showSnackbar(
                                                "save success", "open", duration = SnackbarDuration.Long
                                            )
                                            if (showSnackbar == SnackbarResult.ActionPerformed) {
                                                viewModel.openDownLoadedImage(img.downloadFile(this@PhotoActivity))
                                            }
                                        } else {
                                            scaffoldState.snackbarHostState.showSnackbar("save failure")
                                        }
                                    }
                                }

                            },
                            tint = if (downloaded) Purple500 else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            Icons.Filled.Public,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                startActivity(Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(img.pageURL)
                                })
                            },
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "favorite the image",
                            modifier = Modifier.clickable {
                                if (contains) {
                                    OB.boxFor<Item>().remove(img.id)
                                } else {
                                    img.favoriteDate = System.currentTimeMillis()
                                    OB.boxFor<Item>().put(img)
                                }
                                viewModel.favoriteStateChanged()
                            },
                            tint = if (contains) Purple500 else Color.Gray
                        )
                    },
                    title = {
                    }
                )
                if (!img.tags.isNullOrEmpty()) {
                    BottomAppBar(
                        backgroundColor = Color(0x22000000),
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        elevation = 0.dp
                    ) {
                        Text(text = img.tags, color = Purple500, modifier = Modifier.padding(start = 15.dp))
                    }
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
}