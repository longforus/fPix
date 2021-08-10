package com.longforus.cpix.http

import com.longforus.cpix.util.MyLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Http请求处理类
 * Created by Administrator on 2016-10-14.  框架基本 完全和项目无关
 */

abstract class AbstractHttpManager<HTTP_SERVICE> {
    var httpService: HTTP_SERVICE? = null
        private set

    abstract val isDebug: Boolean


    abstract val baseUrl: String

    protected abstract val interfaceClass: Class<HTTP_SERVICE>

    lateinit var okHttpClient:OkHttpClient

    fun init() {
        //手动创建一个OkHttpClient并设置超时时间
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        //防止读取使用代理,防止被抓包
        builder.proxy(Proxy.NO_PROXY)
        //创建拦截器，记录网络请求的信息，包含 请求/响应行 + 头 + 体
        val logging = MyLoggingInterceptor(getProjectLogger())
        logging.setLevel(MyLoggingInterceptor.Level.BODY)
        builder.addInterceptor(logging)
        okHttpClient = builder.build()
        val retrofit = Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(ResponseConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
        httpService = retrofit.create(interfaceClass)
    }


    open fun getProjectLogger(): MyLoggingInterceptor.Logger {
        return  MyLoggingInterceptor.Logger.DEFAULT
    }


    companion object {
        //private static final Charset UTF8 = Charset.forName("UTF-8");

        const val DEFAULT_TIMEOUT = 40L
    }
}
