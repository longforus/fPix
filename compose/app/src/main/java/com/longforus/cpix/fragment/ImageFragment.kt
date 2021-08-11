package com.longforus.cpix.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.longforus.cpix.R
import com.longforus.cpix.bean.Img
import com.longforus.cpix.typeList
import com.longforus.cpix.ui.theme.CPixTheme
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.viewmodel.ImageViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class ImageFragment : Fragment() {


    private val vm by viewModels<ImageViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ImageScreen()
            }
        }
    }

    @Composable
    private fun ImageScreen() {
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
                    val contentList by vm.imageList.observeAsState()
                    ContentList(contentList ?: emptyList())

                }
            }
        }
    }

    @Composable
    private fun TopImageView(topImage: Img?) {
        Image(
            painter = rememberImagePainter(data = topImage?.webformatURL, builder = {
                crossfade(true)
                scale(Scale.FIT)
            }),
            contentDescription = null,
            modifier = Modifier
                .height(230.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
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
            SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing) , onRefresh = {
                vm.onRefresh()
            }) {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(2),
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.fillMaxWidth(),
                    state = listState,
                ) {
                    itemsIndexed(list) { pos, str ->
                        Image(painter = rememberImagePainter(data = str.webformatURL, builder = {
                            placeholder(R.drawable.placeholder)
                            error(ColorDrawable(android.graphics.Color.GREEN))
                            crossfade(true)
                            scale(Scale.FIT)
                        }), contentDescription = null,
                            modifier = Modifier
                                .clickable {

                                }
                                .height(180.dp)
                                .padding(start = if (pos % 2 == 0) 0.dp else 3.dp, top = 3.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            }
            LaunchedEffect(listState) {
                snapshotFlow {
                    listState.layoutInfo.visibleItemsInfo.last().index * 2 + 2
                }.map {
                    it >= list.size
                }.distinctUntilChanged().filter { it }.collect {
                    vm.loadMore()
                }
            }
        } else {
            Text("loading...",modifier =  Modifier.fillMaxWidth())
        }
    }

    @Composable
    private fun TypeRow(typeList: List<String>, selectIndex: Int = 0, onTabClick: (String, Int) -> Unit) {
        ScrollableTabRow(
            selectedTabIndex = selectIndex,
            backgroundColor = Color(0x22000000),
            edgePadding = 0.dp,
            indicator = {tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectIndex]),
                    color =  Purple500
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
                    Text(text = s,
                        color = Color.White
                    )
                }
            }
        }

    }


}

