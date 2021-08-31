package com.longforus.cpix.viewmodel

import androidx.lifecycle.ViewModel
import com.longforus.cpix.bean.Img
import com.longforus.cpix.bean.OB
import kotlinx.coroutines.flow.MutableStateFlow

class PhotoViewModel(imgID:Long):ViewModel() {
    val favorited = MutableStateFlow(OB.boxFor<Img>().contains(imgID))


}