package com.longforus.cpix.bean

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.longforus.cpix.BuildConfig
import com.longforus.cpix.util.LogUtils
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.BoxStoreBuilder
import io.objectbox.DebugFlags
import io.objectbox.android.AndroidObjectBrowser

/**
 * 数据库
 *
 * @author XQ Yang
 * @date 2018/3/21  9:57
 */
object OB {
    private var sBoxStore: BoxStore? = null
    val sGson: Gson by lazy { Gson() }
    val TAG = "OB"
    fun init(context: Context, builder: BoxStoreBuilder) {
        val boxStoreBuilder = builder.androidContext(context.applicationContext)
        if (BuildConfig.DEBUG) {
//            boxStoreBuilder.debugFlags(DebugFlags.LOG_TRANSACTIONS_WRITE or DebugFlags.LOG_TRANSACTIONS_READ or DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            boxStoreBuilder.debugFlags(DebugFlags.LOG_QUERY_PARAMETERS)
        }
        sBoxStore = boxStoreBuilder.build()
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, sBoxStore?.diagnose())
            openDbBrowser(context)
        }
    }

    fun openDbBrowser(context: Context) {
        val browser = AndroidObjectBrowser(sBoxStore)
        browser.start(context.getApplicationContext())
    }

    fun strMapFromJson(str: String): Map<String, String>? {
        val type = object : TypeToken<Map<String, String>>() {

        }.type
        return sGson.fromJson(str, type)
    }

    fun mapFromJson(str: String): Map<String, Any>? {
        val type = object : TypeToken<Map<String, Any>>() {

        }.type
        return sGson.fromJson(str, type)
    }

    @Deprecated("没有测试过的哦")
    fun <K, V> mapFromJson2(str: String): Map<K, V> {
        val fromJson = sGson.fromJson(str, JsonObject::class.java)
        val map = mutableMapOf<K, V>()
        for (entry in fromJson.entrySet()) {
            val key = entry.key as K
            val value = entry.key as V
            map.put(key, value)
        }
        return map
    }


    fun toJson(o: Any?): String {
        return sGson.toJson(o)
    }

    fun <T> fromJson(str: String, tClass: Class<T>): T {
        return sGson.fromJson(str, tClass)
    }

    inline fun <reified T> fromJson(str: String): T {
        return sGson.fromJson(str, T::class.java)
    }

    inline fun <reified T> listFromJson(str: String): List<T> {
        return Gson().fromJson(str, Array<T>::class.java).toMutableList()
    }

    inline fun <reified T> listFromJson2(str: String): MutableList<T>? {
        val typeToken = object : TypeToken<Array<T>>() {}.type
        return Gson().fromJson<Array<T>>(str, typeToken).toMutableList()
    }


    fun <T> list2Json(list: List<T>?): String {
        return sGson.toJson(list)
    }

    fun <T> list2StringList(list: List<T>?): List<String> {
        if (list != null && list.isNotEmpty()) {
            val type = object : TypeToken<T>() {

            }.type
            val result = ArrayList<String>()
            for (t in list) {
                result.add(sGson.toJson(t, type))
            }
            return result
        }
        return emptyList()
    }

    inline fun <reified T> boxFor(): Box<T> {
        return get().boxFor(T::class.java)
    }


    operator fun <T> Box<T>.plusAssign(t: T) {
        put(t)
    }

    operator fun <T> Box<T>.minusAssign(t: T) {
        remove(t)
    }


    fun get(): BoxStore {
        checkNotNull(sBoxStore) { "OB no routerInit()" }
        return sBoxStore as BoxStore
    }
}

fun Any?.toJson(): String {
    return OB.toJson(this)
}
