package com.longforus.cpix.http

/**
 * 项目的error处理,由projectCore实现
 * @author XQ Yang
 * @date 2017/12/20  18:18
 */
interface ProjectErrorHandler {
    fun onError(throwable: Throwable)
}
