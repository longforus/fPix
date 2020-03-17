package com.longforus.fPix

import android.app.Application
import android.util.Log
import com.idlefish.flutterboost.NewFlutterBoost
import com.idlefish.flutterboost.NewFlutterBoost.BoostLifecycleListener
import com.idlefish.flutterboost.NewFlutterBoost.ConfigBuilder
import com.idlefish.flutterboost.Utils
import com.idlefish.flutterboost.interfaces.INativeRouter
import io.flutter.embedding.android.FlutterView
import io.flutter.plugin.common.MethodChannel

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val router = INativeRouter { context, url, urlParams, requestCode, exts ->
            val assembleUrl = Utils.assembleUrl(url, urlParams)
            PageRouter.openPageByUrl(context, assembleUrl, urlParams)
        }
        val lifecycleListener: BoostLifecycleListener = object : BoostLifecycleListener {
            override fun onEngineCreated() {}
            override fun onPluginsRegistered() {
                val mMethodChannel = MethodChannel(NewFlutterBoost.instance().engineProvider().dartExecutor, "methodChannel")
                Log.e("MyApplication", "MethodChannel create")
                //TextPlatformViewPlugin.register(NewFlutterBoost.instance().getPluginRegistry().registrarFor("TextPlatformViewPlugin"));
            }

            override fun onEngineDestroy() {}
        }
        val platform = ConfigBuilder(this, router)
            .isDebug(true)
            .whenEngineStart(ConfigBuilder.ANY_ACTIVITY_CREATED)
            .renderMode(FlutterView.RenderMode.texture)
            .lifecycleListener(lifecycleListener)
            .build()
        NewFlutterBoost.instance().init(platform)
    }
}