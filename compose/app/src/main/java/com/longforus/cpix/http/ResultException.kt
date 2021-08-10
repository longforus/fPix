package com.longforus.cpix.http

/**
 * Created by XQ Young on 2016-11-28.
 */

open class ResultException(val errCode: String? = "000", msg: String?) : RuntimeException(msg)

class ResponseResultException(errCode: String? = "000", msg: String?) : ResultException(errCode,msg)