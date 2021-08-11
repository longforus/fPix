package com.longforus.cpix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longforus.cpix.bean.ImageListBean
import com.longforus.cpix.bean.Img
import com.longforus.cpix.http.coHttp
import com.longforus.cpix.typeList
import com.longforus.cpix.util.LogUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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


}