package com.longforus.cpix.screen

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.airbnb.lottie.compose.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.longforus.cpix.LocalNavCtrl
import com.longforus.cpix.R
import com.longforus.cpix.bean.Item
import com.longforus.cpix.typeList
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.util.LogUtils
import com.longforus.cpix.viewmodel.ContentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.ceil

private const val TAG = "ImageScreen"

@Composable
fun ContentScreen(usePaging: Boolean, imageVm: ContentViewModel) {
    Column {
        Box(contentAlignment = Alignment.BottomCenter) {
            val topImage by imageVm.topImageUrl.observeAsState()
            TopImageView(topImage, imageVm)
        }
        if (usePaging) {
            val lazyPagingItems = imageVm.imagePager.flow.collectAsLazyPagingItems()
            imageVm.lazyPagingItems = lazyPagingItems
            ContentListPaging(lazyPagingItems) {
                imageVm.doSearch("", true)
            }
        } else {
            val contentList by imageVm.imageList.observeAsState()
            ContentList(contentList ?: emptyList(), imageVm)
        }
    }
}

@Composable
private fun TopImageView(topImage: Item? = null, imageVm: ContentViewModel) {

    val navController = LocalNavCtrl.current!!

    Box(contentAlignment = Alignment.TopEnd) {
        Image(
            painter = rememberImagePainter(data = topImage?.coverImageUrl, builder = {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(ColorDrawable(android.graphics.Color.GREEN))
                scale(Scale.FIT)
            }),
            contentDescription = null,
            modifier = Modifier
                .height(230.dp)
                .clickable {
                    gotoDetailView(topImage, navController)
                }
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(top = 40.dp, end = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_praise))
            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
            )
            Text(text = topImage?.likes?.toString() ?: "  ", color = Color.White, modifier = Modifier.padding(bottom = 3.dp))
        }
    }

    val selectIndex: Int by imageVm.selectTab.observeAsState(0)
    TypeRow(typeList, selectIndex) { title, pos ->
        imageVm.setSelectedTabIndex(pos = pos)
    }
}


private fun gotoDetailView(topImage: Item?, navController: NavHostController) {
    topImage ?: return
    if (topImage.isVideo) {
        navController.navigate(R.id.videoActivity, bundleOf("bean" to topImage))
    } else {
        //使用route不能直接传参 是个bug吧
        //        navController.navigate(R.id.go2Photo,  bundleOf("img" to topImage))
        navController.navigate(R.id.photoActivity, bundleOf("bean" to topImage))
//        context.startActivity(Intent(context, PhotoActivity::class.java).putExtra("bean", topImage))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentList(list: List<Item>, viewModel: ContentViewModel) {
    val listState = rememberLazyListState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val navController = LocalNavCtrl.current!!
    if (list.isNotEmpty()) {
        SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing), onRefresh = {
            viewModel.onRefresh()
        }) {
            val pairs = ArrayList<Pair<Item, Item?>>(ceil(list.size / 2.0).toInt())
            var i = 0
            while (i < list.size) {
                pairs.add(list[i++] to list.getOrNull(i++))
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState,
            ) {
                items(pairs, key = {
                    "${it.first.id}_${it.second?.id}"
                }) { item ->
                    Row(
                        Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .height(180.dp)
                    ) {
                        ItemImage(item.first, true, navController)
                        item.second?.let {
                            ItemImage(it, false, navController)
                        }
                    }
                }

            }
        }
        LaunchedEffect(listState, list) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo.last().index + 1
            }.map {
                LogUtils.d(TAG, "map:$it  listSize=${list.size} ")
                it >= list.size / 2
            }.distinctUntilChanged().filter { it }.collect {
                viewModel.loadMore()
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Purple500)
                Text(
                    "loading...",
                    modifier = Modifier
                        .padding(top = 5.dp),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    color = Purple500
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentListPaging(list: LazyPagingItems<Item>, doRefresh: () -> Unit) {
    val navController = LocalNavCtrl.current!!
    SwipeRefresh(state = rememberSwipeRefreshState(list.loadState.refresh == LoadState.Loading), onRefresh = doRefresh) {
        //LazyPagingItems还不支持grid
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.dp, end = 3.dp),
        ) {
            items(list, key = {
                it.id
            }) { item ->
                item ?: return@items
                Box(Modifier
                    .clickable {
                        gotoDetailView(item, navController)
                    }
                    .height(180.dp)
                    .fillMaxWidth()
                    .padding(top = 3.dp)
                    .clip(RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = rememberImagePainter(data = item.coverImageUrl, builder = {
                            placeholder(R.drawable.placeholder)
                            error(ColorDrawable(android.graphics.Color.GREEN))
                            crossfade(true)
                            scale(Scale.FIT)
                        }), contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    if (item.isVideo) {
                        Icon(
                            Icons.Filled.PlayCircleOutline,
                            contentDescription = null,
                            tint = Purple500,
                            modifier = Modifier.padding(bottom = 5.dp, end = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemImage(item: Item, isLeft: Boolean, navController: NavHostController) {
    Box(Modifier
        .clickable {
            gotoDetailView(item, navController)
        }
        .fillMaxHeight()
        .fillMaxWidth(if (isLeft) 0.5f else 1f)
        .padding(start = if (isLeft) 0.dp else 3.dp, top = 3.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = rememberImagePainter(data = item.coverImageUrl, builder = {
                placeholder(R.drawable.placeholder)
                error(ColorDrawable(android.graphics.Color.RED))
                crossfade(true)
                scale(Scale.FIT)
            }), contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        if (item.isVideo) {
            Icon(
                Icons.Filled.PlayCircleOutline,
                contentDescription = null,
                tint = Purple500,
                modifier = Modifier.padding(bottom = 5.dp, end = 5.dp)
            )
        }
    }
}


@Composable
private fun TypeRow(typeList: List<String>, selectIndex: Int = 0, onTabClick: (String, Int) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = selectIndex,
        backgroundColor = Color(0x22000000),
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectIndex]),
                color = Purple500
            )
        }
    ) {
        typeList.forEachIndexed { index, s ->
            Tab(
                selected = index == selectIndex,
                onClick = {
                    onTabClick(s, index)
                },
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = s,
                    color = Color.White
                )
            }
        }
    }

}