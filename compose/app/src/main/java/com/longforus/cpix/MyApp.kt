package com.longforus.cpix

import android.app.Application
import com.longforus.cpix.http.ProjectCoreHttpManager
import com.longforus.cpix.util.LogUtils

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        LogUtils.Builder(this).setBorderSwitch(false)
        ProjectCoreHttpManager.init()
    }
}