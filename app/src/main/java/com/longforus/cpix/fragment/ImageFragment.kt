package com.longforus.cpix.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.longforus.cpix.typeList
import com.longforus.cpix.viewmodel.ImageViewModel

class ImageFragment : Fragment() {


    val vm by viewModels<ImageViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ImageScreen()
            }
        }
    }

    @Composable
    @Preview
    private fun ImageScreen() {
        MaterialTheme {
            Scaffold(floatingActionButton = {
                Button(
                    onClick = { /*TODO*/ },
                    shape = CircleShape,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Image(painter = painterResource(id = R.drawable.ic_menu_search), contentDescription = null)
                }
            }) {
                Column {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        Image(
                            painter = ColorPainter(Color.Green),
                            contentDescription = null,
                            modifier = Modifier
                                .height(230.dp)
                                .fillMaxWidth()
                        )
                        val selectIndex: Int by vm.selectTab.observeAsState(0)
                        TypeRow(typeList, selectIndex) { title, pos ->
                            vm.setSelectedTabIndex(pos = pos)
                        }


                    }
                    val contentList by vm.imageList.observeAsState(kotlin.run {
                        val list = mutableListOf<String>()
                        repeat(10) {
                            list.add(it.toString())
                        }
                        list
                    })
                    ContentList(contentList)

                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ContentList(list: List<String>) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = PaddingValues(2.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(list) { pos, str ->
//                Text(text = str)
                Image(painter = ColorPainter(Color.Blue), contentDescription = null, modifier = Modifier
                    .clickable {

                    }
                    .height(200.dp)
                    .padding(start = if (pos % 2 == 0) 0.dp else 5.dp, top = 5.dp))
            }

        }
    }

    @Composable
    private fun TypeRow(typeList: List<String>, selectIndex: Int = 0, onTabClick: (String, Int) -> Unit) {
        ScrollableTabRow(
            selectedTabIndex = selectIndex,
            backgroundColor = Color.Transparent,
            edgePadding = 0.dp,
        ) {
            typeList.forEachIndexed { index, s ->
                Tab(
                    selected = index == selectIndex,
                    onClick = {
                        onTabClick(s, index)
                    },
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = s)
                }
            }
        }

    }


}