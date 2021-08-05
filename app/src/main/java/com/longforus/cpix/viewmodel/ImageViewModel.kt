package com.longforus.cpix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel:ViewModel() {
    private val _selectTab = MutableLiveData<Int>(0)
    val selectTab:LiveData<Int> = _selectTab

    fun setSelectedTabIndex(pos:Int){
        _selectTab.value = pos
    }

    val imageList = MutableLiveData<List<String>>()

}