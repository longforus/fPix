package com.longforus.cpix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.longforus.cpix.bean.ContentListBean
import com.longforus.cpix.bean.Item
import com.longforus.cpix.bean.Item_
import com.longforus.cpix.bean.OB
import com.longforus.cpix.http.coHttp
import com.longforus.cpix.util.LogUtils
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.query
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FavoriteViewModel : ViewModel() {

    val TAG = "FavoriteViewModel"

    val imageList = ObjectBoxLiveData<Item>(OB.boxFor<Item>().query {
        orderDesc(Item_.favoriteDate)
    }).switchMap { list ->
        liveData {
            val now = System.currentTimeMillis()
            val partition = list.partition {
                now - it.lastUpdateDate > 23 * 60 * 60 * 1000
            }
            if (partition.first.isNotEmpty()) {
                val newList = ArrayList<Item?>(partition.first.size)
                partition.first.asFlow().map { old ->
                    coHttp<ContentListBean> {
                        api { service, parameter ->
                            parameter["id"] = old.id
                            service.getImages(parameter.build())
                        }
                    }?.run {
                        LogUtils.d(TAG, this)
                        if (hits.isNotEmpty()) {
                            hits[0].apply {
                                favoriteDate = old.favoriteDate
                            }
                        } else {
                            old
                        }
                    }
                }.toCollection(newList)
                OB.boxFor<Item>().put(newList)
                emit((partition.second + newList).sortedByDescending {
                    it?.favoriteDate
                })
            } else {
                emit(list)
            }
        }
    }


}