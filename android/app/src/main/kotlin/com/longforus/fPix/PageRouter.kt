package com.longforus.fPix

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.idlefish.flutterboost.containers.BoostFlutterActivity
import java.util.*

object PageRouter {
    val pageName: MutableMap<String, String> = mutableMapOf()
    const val NATIVE_PAGE_URL = "sample://nativePage"
    const val FLUTTER_PAGE_URL = "sample://flutterPage"
    const val FLUTTER_FRAGMENT_PAGE_URL = "sample://flutterFragmentPage"


    init {
        pageName.put("first", "first")
        pageName.put("second", "second")
        pageName.put("tab", "tab")
        pageName.put("sample://flutterPage", "flutterPage")
    }

    @JvmOverloads
    fun openPageByUrl(context: Context, url: String, params: Map<*, *>?, requestCode: Int = 0): Boolean {
        val path = url.split("\\?").toTypedArray()[0]
        Log.i("openPageByUrl", path)
        return try {
            if (pageName.containsKey(path)) {
                val intent = BoostFlutterActivity.withNewEngine().url(pageName[path]!!).params(params!!)
                    .backgroundMode(BoostFlutterActivity.BackgroundMode.opaque).build(context)
                if (context is Activity) {
                    context.startActivityForResult(intent, requestCode)
                } else {
                    context.startActivity(intent)
                }
                return true
            } else if (url.startsWith(FLUTTER_FRAGMENT_PAGE_URL)) {
                //context.startActivity(new Intent(context, FlutterFragmentPageActivity.class));
                return true
            } else if (url.startsWith(NATIVE_PAGE_URL)) {
                context.startActivity(Intent(context, NativePageActivity::class.java))
                return true
            }
            false
        } catch (t: Throwable) {
            false
        }
    }
}