package com.longforus.cpix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longforus.cpix.bean.Img
import com.longforus.cpix.bean.OB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhotoViewModel(val imgID:Long):ViewModel() {

    fun favoriteStateChanged() {
        viewModelScope.launch {
            favorited.emit(OB.boxFor<Img>().contains(imgID))
        }
    }

    val favorited = MutableStateFlow(OB.boxFor<Img>().contains(imgID))


}