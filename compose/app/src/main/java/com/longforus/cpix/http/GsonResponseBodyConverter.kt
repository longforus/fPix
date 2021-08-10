package com.longforus.cpix.http

import com.google.gson.Gson
import com.longforus.cpix.util.LogUtils
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type
import kotlin.math.min

/**
 * 解析响应回来的数据，当接口为非正常状态时抛出异常，不再将data转成json
 * Created by XQ Young on 2016-11-28.
 */

internal class GsonResponseBodyConverter<T>(private val gson: Gson, private val type: Type) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val response = value.string()
        if (response.length > 3000) {
            val stringBuilder = StringBuilder()
            var i = 0
            var j = 250
            while (j < response.length) {
                stringBuilder.append(response.substring(i, j))
                i =j
                j += min(250,response.length-1)
                stringBuilder.append("\n")
            }
            if (j == 250) {
                stringBuilder.append(response)
            }
            stringBuilder.append("\n")
            LogUtils.d(TAG, stringBuilder.toString())
        } else {
            LogUtils.json(TAG, response)
        }
        LogUtils.v(TAG, "type = $type")
        //ResultResponse 只解析result字段 ,因为huawen的后台数据只有一层,這里就不校验了
//        val `object` = gson.fromJson(response, JsonObject::class.java)
//        val code = `object`.get("code").asString
//        return if (CORRECT_CODE == code) {
//            gson.fromJson<T>(response, type)
//        } else {
//            throw ResultException(code, `object`.get("message").asString)
//        }

        return gson.fromJson<T>(response, type)
    }
    val TAG = "GsonResponseBodyConverter"
}


