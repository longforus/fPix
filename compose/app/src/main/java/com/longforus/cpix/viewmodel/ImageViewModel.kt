package com.longforus.cpix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.longforus.cpix.bean.ImageListBean
import com.longforus.cpix.bean.Img
import com.longforus.cpix.http.*
import com.longforus.cpix.typeList
import com.longforus.cpix.util.LogUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.*

class ImageViewModel : ViewModel() {
    val TAG = "ImageViewModel"
    private val _selectTab = MutableLiveData<Int>(0)
    val selectTab: LiveData<Int> = _selectTab

    val topImageUrl = MutableLiveData<Img>()
    val isRefreshing = MutableStateFlow(false)
    private val pageSize = 20
    private var currentPageIndex = 1
    private var imageType = typeList[0]
    private var keyWord = ""


    fun setSelectedTabIndex(pos: Int) {
        if (pos != _selectTab.value) {
            _selectTab.value = pos
            imageType = typeList[pos]
            currentPageIndex = 1
            keyWord = ""
            if (isRefreshing.value) {
                isRefreshing.tryEmit(false)
            }
            loadMore()
        }
    }


    init {
        loadMore()
    }


    fun loadMore() {
        Log.d(TAG, "on load more")
        viewModelScope.launch {
            coHttp<ImageListBean> {
                api { service, parameter ->
                    parameter["category"] = imageType.lowercase(Locale.getDefault())
                    parameter["page"] = currentPageIndex
                    parameter["per_page"] = pageSize
                    if (keyWord.isNotEmpty()) {
                        parameter["q"] = keyWord
                    }
                    service.getImages(parameter.build())
                }

                onError {
                    it.printStackTrace()
                    isRefreshing.emit(false)
                }
            }?.let {
                LogUtils.d(TAG, it)
                if (currentPageIndex == 1) {
                    topImageUrl.value = it.hits[0]
                    imageList.value = it.hits.drop(1)
                } else {
                    val toMutableList = imageList.value?.toMutableList()
                    if (toMutableList.isNullOrEmpty()) {
                        imageList.value = it.hits
                    } else {
                        imageList.value = toMutableList + it.hits
                    }
                }
                isRefreshing.emit(false)
                currentPageIndex++
            }
        }
    }

    fun onRefresh() {
        if (!isRefreshing.value) {
            isRefreshing.tryEmit(true)
            currentPageIndex = 1
            keyWord = ""
            loadMore()
        }
    }

    val imageList = MutableLiveData<List<Img>>()


    val imagePager = Pager<Int, Img>(PagingConfig(pageSize, prefetchDistance = 20), initialKey = currentPageIndex) {
        ImgPageSources()
    }



    inner class ImgPageSources : PagingSource<Int, Img>() {

        override fun getRefreshKey(state: PagingState<Int, Img>): Int? {
            // Try to find the page key of the closest page to anchorPosition, from
            // either the prevKey or the nextKey, but you need to handle nullability
            // here:
            //  * prevKey == null -> anchorPage is the first page.
            //  * nextKey == null -> anchorPage is the last page.
            //  * both prevKey and nextKey null -> anchorPage is the initial page, so
            //    just return null.
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Img> {

            val i = params.key ?: 1
            val bean = coHttp<ImageListBean> {
                api { service, parameter ->
                    parameter["category"] = imageType.lowercase(Locale.getDefault())
                    parameter["page"] = i
                    parameter["per_page"] = params.loadSize
                    if (keyWord.isNotEmpty()) {
                        parameter["q"] = keyWord
                    }
                    service.getImages(parameter.build())
                }

                onError {
                    it.printStackTrace()
                    isRefreshing.emit(false)
                }
            } ?: return LoadResult.Error(RuntimeException("load error"))

            LogUtils.d(TAG, bean)
            val list: List<Img>
            if (i == 1) {
                topImageUrl.value = bean.hits[0]
                list = bean.hits.drop(1)
            } else {
                list = bean.hits
            }
            isRefreshing.emit(false)
            return LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = i + 1
            )
        }

    }
}
