package com.longforus.cpix.http

import com.longforus.cpix.bean.ImageListBean
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * @author XQ Yang
 * @date 2017/12/1  16:51
 */
interface ProjectCoreHttpService {

    @GET("/api")
    suspend fun getImages(@QueryMap map: Map<String, String>): retrofit2.Response<ImageListBean>

}
