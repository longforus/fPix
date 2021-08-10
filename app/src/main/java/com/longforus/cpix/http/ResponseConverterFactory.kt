package com.longforus.cpix.http

import com.google.gson.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 重写ConverterFactory，调用我们自己的返回信息转换类
 * Created by XQ Young on 2016-11-28.
 */

class ResponseConverterFactory private constructor(private val gson: Gson?) : Converter.Factory() {

    init {
        if (gson == null) {
            throw NullPointerException("gson == null")
        }
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        //TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return GsonResponseBodyConverter<Any>(gson!!, type!!)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?,
        retrofit: Retrofit?): Converter<*, RequestBody>? {
        return GsonRequestBodyConverter<Any>(gson!!, type!!)
    }

    companion object {

        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads
        fun create(gson: Gson = GsonBuilder().registerTypeAdapter(String::class.java, object :
            JsonDeserializer<String> {
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String {
                if (json == null) {
                    return ""
                }
                return json.asString
            }
        }).create()): ResponseConverterFactory {
            return ResponseConverterFactory(gson)
        }
    }
}
/**
 * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
 * decoding from JSON (when no charset is specified by a header) will use UTF-8.
 */
