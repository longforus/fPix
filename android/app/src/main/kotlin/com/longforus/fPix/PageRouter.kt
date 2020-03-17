package com.longforus.fPix

import android.content.Context
import android.content.Intent
import android.util.Log
import com.idlefish.flutterboost.containers.NewBoostFlutterActivity

object PageRouter {

    const val NATIVE_PAGE_URL = "sample://nativePage"
    const val FLUTTER_PAGE_URL = "sample://homePage"
    const val FLUTTER_FRAGMENT_PAGE_URL = "sample://flutterFragmentPage"
    val pageName: Map<String, String?> = object : HashMap<String, String?>() {
        init {
            put("first", "first")
            put("second", "second")
            put("tab", "tab")
            put(FLUTTER_PAGE_URL, "homePage")
        }
    }
    @JvmOverloads
    fun openPageByUrl(context: Context, url: String, params: Map<*, *>?, requestCode: Int = 0): Boolean {
        val path = url.split("\\?").toTypedArray()[0]
        Log.i("openPageByUrl", path)
        try {
            when {
                pageName.containsKey(path) -> {
                    val intent = NewBoostFlutterActivity.withNewEngine().url(pageName[path]!!).params(params!!)
                        .backgroundMode(NewBoostFlutterActivity.BackgroundMode.opaque).build(context)
                    context.startActivity(intent)
                }
                url.startsWith(FLUTTER_FRAGMENT_PAGE_URL) -> { //context.startActivity(new Intent(context, FlutterFragmentPageActivity.class));
                    return true
                }
                url.startsWith(NATIVE_PAGE_URL) -> {
                    val intent = Intent(context, NativePageActivity::class.java)
                    context.startActivity(intent)
                    return true
                }
                else -> {
                    return false
                }
            }
        } catch (t: Throwable) {
            return false
        }
        return false
    }
}