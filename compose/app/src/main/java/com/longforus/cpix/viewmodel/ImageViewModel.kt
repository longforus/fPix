package com.longforus.cpix.viewmodel

import com.longforus.cpix.bean.ContentListBean
import com.longforus.cpix.http.*
import retrofit2.Response
import java.util.*

class ImageViewModel : ContentViewModel() {
     val TAG = "ImageViewModel"
    override suspend fun getRequest(
        parameter: RequestMap,
        service: ProjectCoreHttpService,
        pageIndex: Int
    ): Response<ContentListBean> {
        parameter["category"] = imageType.lowercase(Locale.getDefault())
        parameter["page"] = pageIndex
        parameter["per_page"] = pageSize
        if (keyWord.isNotEmpty()) {
            parameter["q"] = keyWord
        }
        return service.getImages(parameter.build())
    }

}
