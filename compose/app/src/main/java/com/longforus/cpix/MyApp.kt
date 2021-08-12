package com.longforus.cpix

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import com.longforus.cpix.http.ProjectCoreHttpManager
import com.longforus.cpix.util.LogUtils
import okhttp3.OkHttpClient

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.Builder(this).setBorderSwitch(false)
        ProjectCoreHttpManager.init()

        val imageLoader = ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .build()
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}