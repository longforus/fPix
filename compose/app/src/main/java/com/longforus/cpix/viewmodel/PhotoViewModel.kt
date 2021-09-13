package com.longforus.cpix.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longforus.cpix.bean.Item
import com.longforus.cpix.bean.OB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toBitmap
import com.longforus.cpix.MyApp
import com.longforus.cpix.util.LogUtils
import com.longforus.cpix.util.getOpenFileIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File


class PhotoViewModel(val imgID: Long) : ViewModel() {

    val TAG = "PhotoViewModel"

    fun favoriteStateChanged() {
        viewModelScope.launch {
            favorited.emit(OB.boxFor<Item>().contains(imgID))
        }
    }

    suspend fun saveImage(drawable: Drawable?, fileName: String): Boolean {
        return viewModelScope.async(Dispatchers.IO) {
           return@async MyApp.app.externalCacheDir?.let {
                val file = File(it, fileName)
                LogUtils.d(TAG, "${file.absolutePath}")
                val outputStream = file.outputStream()
                outputStream.use { outputStream ->
                     drawable?.toBitmap()?.compress(Bitmap.CompressFormat.PNG, 100, outputStream) ?: false
                }
            } ?: false
        }.await()
    }

    fun openDownLoadedImage(name: String) {
        MyApp.app.startActivity(getOpenFileIntent(File(MyApp.app.externalCacheDir,name),MyApp.app,"image/jpeg"))
    }

    val favorited = MutableStateFlow(OB.boxFor<Item>().contains(imgID))


}