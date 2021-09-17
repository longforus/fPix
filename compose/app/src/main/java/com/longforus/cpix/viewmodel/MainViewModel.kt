package com.longforus.cpix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
private const val USING_PAGING_KEY = "using_paging_key"

class MainViewModel:ViewModel() {


    fun onUsingPagingChanged(using: Boolean) {
        usePaging.value = using
        MMKV.defaultMMKV().putBoolean(USING_PAGING_KEY,using)
    }

    val usePaging = MutableLiveData(MMKV.defaultMMKV().decodeBool(USING_PAGING_KEY,false))

}