package com.longforus.cpix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel:ViewModel() {
    val TAG = "ImageViewModel"
    private val _selectTab = MutableLiveData<Int>(0)
    val selectTab:LiveData<Int> = _selectTab

    val topImageUrl = MutableLiveData<String>("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farticle%2F2a1e91dde2d9b4e6d66293663132decffe1c4f2e.jpg&refer=http%3A%2F%2Fi0.hdslb.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631153328&t=95ddfdc85b6f7c58df117149597ae61e")

    fun setSelectedTabIndex(pos:Int){
        _selectTab.value = pos
    }

    fun loadMore() {
        Log.d(TAG,"on load more")
    }

    val imageList = MutableLiveData<List<String>>()




}