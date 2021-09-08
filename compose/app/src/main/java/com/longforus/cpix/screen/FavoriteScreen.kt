package com.longforus.cpix.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.longforus.cpix.LocalNavCtrl
import com.longforus.cpix.bean.Item
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.viewmodel.FavoriteViewModel
import kotlin.math.ceil

@Composable
fun FavoriteScreen(){
    val viewModel: FavoriteViewModel = viewModel()
    val contentList by viewModel.imageList.observeAsState()
    val listState = rememberLazyListState()
    val navController = LocalNavCtrl.current!!
    if (!contentList.isNullOrEmpty()) {
        contentList?.let { list ->
            val pairs = ArrayList<Pair<Item?, Item?>>(ceil(list.size / 2.0).toInt())
            var i = 0
            while (i < list.size) {
                pairs.add(list[i++] to list.getOrNull(i++))
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 33.dp),
                state = listState,
            ) {
                items(pairs, key = {
                    "${it.first?.id}_${it.second?.id}"
                }) { item ->
                    Row(
                        Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .height(180.dp)
                    ) {
                        item.first?.let {
                            ItemImage(it, true,navController)
                        }
                        item.second?.let {
                            ItemImage(it, false,navController)
                        }
                    }
                }

            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Favorite is empty...",
                modifier = Modifier
                    .padding(top = 5.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Purple500
            )
        }

    }
}