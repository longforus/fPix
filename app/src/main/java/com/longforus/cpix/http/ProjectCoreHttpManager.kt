package com.longforus.cpix.http

import com.longforus.cpix.util.LogUtils

/**
 * @author XQ Yang
 * @date 2017/12/1  16:53
 */
object ProjectCoreHttpManager : AbstractHttpManager<ProjectCoreHttpService>() {



    override val interfaceClass: Class<ProjectCoreHttpService>
        get() = ProjectCoreHttpService::class.java

    override val isDebug: Boolean
        get() = LogUtils.sLogSwitch



    override val baseUrl: String = "https://pixabay.com"





}
