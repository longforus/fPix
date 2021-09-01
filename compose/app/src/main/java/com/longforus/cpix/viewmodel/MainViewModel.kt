package com.longforus.cpix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV

class MainViewModel:ViewModel() {

    private val USING_PAGING_KEY = "using_paging_key"

    fun onUsingPagingChanged(using: Boolean) {
        usePaging.value = using
        MMKV.defaultMMKV().putBoolean(USING_PAGING_KEY,using)
    }

    val usePaging = MutableLiveData(MMKV.defaultMMKV().decodeBool(USING_PAGING_KEY,false))

}