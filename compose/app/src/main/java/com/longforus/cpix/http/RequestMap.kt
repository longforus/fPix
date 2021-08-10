package com.longforus.cpix.http

import com.google.gson.Gson
import okhttp3.MediaType
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by XQ Yang on 2017/9/6  16:20.
 * Description : 通用参数工厂
 */

class RequestMap{

    protected val map: MutableMap<String, String> = TreeMap()

    private var mFileMap: MutableMap<String, File>? = null


    fun getMediaType(file: File): MediaType? {
        val absolutePath = file.absolutePath
        return when {
            absolutePath.endsWith(".png", true) -> MediaType.parse("image/png")
            absolutePath.endsWith(".jpg", true) -> MediaType.parse("image/jpeg")
            absolutePath.endsWith(".jpeg", true) -> MediaType.parse("image/jpeg")
            else -> MediaType.parse("multipart/form-data")
        }
    }


    fun put(vararg p: Pair<String, Any>): RequestMap {
        for (pair in p) {
            when (pair.second) {
                is CharSequence, is Number -> {
                    map[pair.first] = pair.second.toString()
                }
                is File -> {
                    if (mFileMap == null) {
                        mFileMap = HashMap()
                    }
                    mFileMap!![pair.first] = pair.second as File
                }
                else -> {
                    map[pair.first] = mGson.toJson(pair.second)
                }
            }
        }
        return this
    }

    fun build(): Map<String, String> {
        map["key"] = KEY
        return map
    }



    fun put(map: Map<String, String>): RequestMap {
        this.map.putAll(map)
        return this
    }

    operator fun get(key: String): String? {
        return map[key]
    }

    operator fun set(key: String, value: Any?) {
        put(key to (value ?: ""))
    }

    override fun toString(): String {
        return "RequestMap(map=$map, mFileMap=$mFileMap)"
    }


    companion object {
        const val KEY = "11042541-60a032dcf49543f53d415848c"
        val mGson: Gson by lazy { Gson() }

    }

}
