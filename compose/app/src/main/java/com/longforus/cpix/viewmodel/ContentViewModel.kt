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
import com.longforus.cpix.bean.ContentListBean
import com.longforus.cpix.bean.Item
import com.longforus.cpix.http.*
import com.longforus.cpix.typeList
import com.longforus.cpix.util.LogUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.RuntimeException
import java.util.*

abstract class ContentViewModel : ViewModel() {
    private val _selectTab = MutableLiveData<Int>(0)
    val selectTab: LiveData<Int> = _selectTab

    val topImageUrl = MutableLiveData<Item>()
    val isRefreshing = MutableStateFlow(false)
    protected val pageSize = 20
    protected var currentPageIndex = 1
    protected var imageType = typeList[0]
    protected var keyWord = ""


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
            coHttp<ContentListBean> {
                api { service, parameter ->
                    getRequest(parameter, service, currentPageIndex)
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

    abstract suspend fun getRequest(
        parameter: RequestMap,
        service: ProjectCoreHttpService,
        pageIndex: Int
    ): Response<ContentListBean>


    fun onRefresh() {
        if (!isRefreshing.value) {
            isRefreshing.tryEmit(true)
            currentPageIndex = 1
            keyWord = ""
            loadMore()
        }
    }

    val imageList = MutableLiveData<List<Item>>()


    val imagePager = Pager<Int, Item>(PagingConfig(pageSize, prefetchDistance = 20), initialKey = currentPageIndex) {
        ImgPageSources()
    }



    inner class ImgPageSources : PagingSource<Int, Item>() {

        override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
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

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {

            val i = params.key ?: 1
            val bean = coHttp<ContentListBean> {
                api { service, parameter ->
                    getRequest(parameter,service,i)
                }

                onError {
                    it.printStackTrace()
                    isRefreshing.emit(false)
                }
            } ?: return LoadResult.Error(RuntimeException("load error"))

            LogUtils.d(TAG, bean)
            val list: List<Item>
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
