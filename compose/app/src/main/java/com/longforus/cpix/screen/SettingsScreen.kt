package com.longforus.cpix.screen


import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.longforus.cpix.MyApp
import com.longforus.cpix.util.getOpenDirIntent
import com.longforus.cpix.viewmodel.MainViewModel
import java.io.File

@Composable
fun SettingsScreen(viewModel: MainViewModel, go2GitHub: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 33.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)
    ) {
        val (topColumn, about) = createRefs()
        Column(
            Modifier
                .constrainAs(topColumn) {
                    top.linkTo(parent.top)
                }
                .verticalScroll(scrollState)) {
            val usingPaging by viewModel.usePaging.observeAsState()
            UsingPagingRow(usingPaging ?: false, viewModel, Modifier.height(60.dp))
            Text(text = "Image download path:")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                val dir =  MyApp.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: File("/")
                ClickableText(
                    text = AnnotatedString(
                        dir.absolutePath, spanStyle = SpanStyle(
                            color = Color.Blue, textDecoration =
                            TextDecoration.Underline
                        )
                    ), onClick = {
                        MyApp.app.filesDir?.let {
                            MyApp.app.startActivity(getOpenDirIntent(dir, MyApp.app))
                        }
                    }
                )
            }
        }
        Column(Modifier
            .constrainAs(about) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(bottom = 100.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("long for us")
            Spacer(modifier = Modifier.height(5.dp))
            ClickableText(
                text = AnnotatedString(
                    "https://github.com/longforus/fPix", spanStyle = SpanStyle(
                        color = Color.Blue, textDecoration =
                        TextDecoration.Underline
                    )
                ), onClick = go2GitHub
            )
        }
    }
}

@Composable
private fun UsingPagingRow(using: Boolean, viewModel: MainViewModel, modifier: Modifier) {
    Row(
        modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Change grid to paging list")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)
        ) {
            Switch(checked = using, onCheckedChange = {
                viewModel.onUsingPagingChanged(it)
            })
        }

    }
}