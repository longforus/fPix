package com.longforus.cpix.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
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


class PhotoViewModel(val img: Item) : ViewModel() {

    val downloaded = MutableStateFlow(img.downloadFile(MyApp.app).exists())
    val TAG = "PhotoViewModel"

    fun favoriteChange() {
        if (favorited.value) {
            OB.boxFor<Item>().remove(img.id)
        } else {
            img.favoriteDate = System.currentTimeMillis()
            OB.boxFor<Item>().put(img)
        }
        viewModelScope.launch {
            favorited.emit(OB.boxFor<Item>().contains(img.id))
        }
    }

    suspend fun saveImage(drawable: Drawable?,file: File): Boolean {
        return viewModelScope.async(Dispatchers.IO) {
            val b = MyApp.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
                LogUtils.d(TAG, "${file.absolutePath}")
                if (file.exists()) {
                    return@async true
                }
                val outputStream = file.outputStream()
                outputStream.use { outputStream ->
                    drawable?.toBitmap()?.compress(Bitmap.CompressFormat.PNG, 100, outputStream) ?: false
                }
            } ?: false
            downloaded.tryEmit(b)
            return@async b
        }.await()
    }

    fun openDownLoadedImage(file: File) {
        MyApp.app.startActivity(
            getOpenFileIntent(
                file,
                MyApp.app,
                "image/jpeg"
            )
        )
    }

    val favorited = MutableStateFlow(OB.boxFor<Item>().contains(img.id))


}