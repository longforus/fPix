package com.longforus.cpix.http

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type

/**
 * body 请求工厂
 * Created by XQ Young on 2017-11-9 17:35:56.
 */

internal class GsonRequestBodyConverter<T>(private val gson: Gson, private val type: Type) : Converter<T, RequestBody> {
    private val mParse: MediaType? = MediaType.parse("application/json; charset=utf-8")

//    var mGson = GsonBuilder().registerTypeAdapter(String::class.java,object :TypeAdapter<String>(){
//        override fun write(out: JsonWriter?, value: String?) {
//
//        }
//
//        override fun read(`in`: JsonReader?): String {
//
//        }
//
//    }).serializeNulls().addSerializationExclusionStrategy(object : ExclusionStrategy{
//        override fun shouldSkipClass(clazz: Class<*>?): Boolean  = false
//
//        override fun shouldSkipField(f: FieldAttributes?): Boolean {
//           return f?.declaredClass==String::class.java &&
//        }
//
//    })


    @Throws(IOException::class)
    override fun convert(value: T): RequestBody? {
        return RequestBody.create(mParse, gson.toJson(value))
    }
}
