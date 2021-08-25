package com.longforus.cpix.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.longforus.cpix.PhotoActivity
import com.longforus.cpix.R
import com.longforus.cpix.bean.Img
import com.longforus.cpix.typeList
import com.longforus.cpix.ui.theme.CPixTheme
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.util.LogUtils
import com.longforus.cpix.viewmodel.ImageViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.ceil

class ImageFragment : Fragment() {

    val TAG = "ImageFragment"
    private val vm by viewModels<ImageViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val usePaging by vm.usePaging.observeAsState()
                ImageScreen(usePaging ?: false)
            }
        }
    }

    @Composable
    private fun ImageScreen(usePaging: Boolean) {
        CPixTheme {
            Scaffold(floatingActionButton = {
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(painter = rememberVectorPainter(image = Icons.Filled.Search), contentDescription = null)
                }
            }) {
                Column {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        val topImage by vm.topImageUrl.observeAsState()
                        TopImageView(topImage)
                    }
                    if (usePaging) {
                        val lazyPagingItems = vm.imagePager.flow.collectAsLazyPagingItems()
                        ContentListPaging(lazyPagingItems)
                    } else {
                        val contentList by vm.imageList.observeAsState()
                        ContentList(contentList ?: emptyList())
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showSystemUi = true)
    private fun TopImageView(topImage: Img? = null) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(
                painter = rememberImagePainter(data = topImage?.webformatURL, builder = {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(ColorDrawable(android.graphics.Color.GREEN))
                    scale(Scale.FIT)
                }),
                contentDescription = null,
                modifier = Modifier
                    .height(230.dp)
                    .clickable {
                        startActivity(Intent(requireContext(), PhotoActivity::class.java).putExtra("bean", topImage))
                    }
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Row(horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(top = 40.dp,end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = rememberVectorPainter(image = Icons.Filled.ThumbUpAlt),
                    contentDescription = null,
                    tint = Purple500
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = topImage?.likes?.toString() ?: "  ",color = Color.White)
            }
        }

        val selectIndex: Int by vm.selectTab.observeAsState(0)
        TypeRow(typeList, selectIndex) { title, pos ->
            vm.setSelectedTabIndex(pos = pos)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ContentList(list: List<Img>) {

        val listState = rememberLazyListState()
        val isRefreshing by vm.isRefreshing.collectAsState()
        if (list.isNotEmpty()) {
            SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing), onRefresh = {
                vm.onRefresh()
            }) {
                val pairs = ArrayList<Pair<Img, Img?>>(ceil(list.size / 2.0).toInt())
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
                            ItemImage(item.first, true)
                            item.second?.let {
                                ItemImage(it, false)
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
                    vm.loadMore()
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
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
    private fun ContentListPaging(list: LazyPagingItems<Img>) {
        SwipeRefresh(state = rememberSwipeRefreshState(list.loadState.refresh == LoadState.Loading), onRefresh = {
            list.refresh()
        }) {
            //LazyPagingItems还不支持grid
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp, end = 3.dp),
            ) {
                items(list, key = {
                    it.id
                }) { img ->
                    img ?: return@items
                    Image(painter = rememberImagePainter(data = img.webformatURL, builder = {
                        placeholder(R.drawable.placeholder)
                        error(ColorDrawable(android.graphics.Color.GREEN))
                        crossfade(true)
                        scale(Scale.FIT)
                    }), contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                startActivity(Intent(requireContext(), PhotoActivity::class.java).putExtra("bean", img))
                            }
                            .height(180.dp)
                            .fillMaxWidth()
                            .padding(top = 3.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    @Composable
    private fun ItemImage(img: Img, isLeft: Boolean) {
        Image(painter = rememberImagePainter(data = img.webformatURL, builder = {
            placeholder(R.drawable.placeholder)
            error(ColorDrawable(android.graphics.Color.GREEN))
            crossfade(true)
            scale(Scale.FIT)
        }), contentDescription = null,
            modifier = Modifier
                .clickable {
                    startActivity(Intent(requireContext(), PhotoActivity::class.java).putExtra("bean", img))
                }
                .fillMaxHeight()
                .fillMaxWidth(if (isLeft) 0.5f else 1f)
                .padding(start = if (isLeft) 0.dp else 3.dp, top = 3.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
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


}

