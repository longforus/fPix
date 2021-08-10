package com.longforus.cpix.http

import android.view.View
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import java.lang.ref.WeakReference


sealed class Either

class Succ<T>(val result: T):Either()
class Fail( val result:Exception):Either()

val TAG = "CoNetExtFuns"

class CoRequestWrap<BEAN, SERVICE, T> {
    var requestMap: RequestMap? = null
    internal var loadingMsgLive: MutableLiveData<String?>? = null
    private var stateFlow: MutableStateFlow<RequestState>? = null
    private var needLockView: WeakReference<View>? = null
    internal var needGlobalExceptionProcess = true
    internal lateinit var apiSelect: suspend (service: SERVICE, map: RequestMap) -> Response<BEAN>
    internal var success: suspend ((result: BEAN) -> T?) = {
        null
    }
    internal var error: suspend ((e: Throwable) -> T?) = {
        null
    }
    private var loadingMsg: String? = null

    internal var defaultT: T? = null

    fun requestMap(parameter: RequestMap?) {
        this.requestMap = parameter
    }

    fun needGlobalExceptionProcess(need: Boolean) {
        this.needGlobalExceptionProcess = need
    }

    fun defaultReturn(default: T) {
        defaultT = default
    }


    fun loadingMsgLive(loadingMsgLive: MutableLiveData<String?>, loadingMsg: String = "读取") {
        this.loadingMsgLive = loadingMsgLive
        this.loadingMsg = loadingMsg
    }


    fun stateFlow(): StateFlow<RequestState> {
        this.stateFlow = MutableStateFlow(RequestState.NO_REQUEST)
        return stateFlow!!
    }

    /**
     * 防止连点 该view只有在返回后才能再点击.
     */
    fun lockView(view: View) {
        needLockView = WeakReference(view)
    }

    fun api(api: suspend (service: SERVICE, parameter: RequestMap) -> Response<BEAN>) {
        this.apiSelect = api
    }

    fun onSuccess(success: suspend ((result: BEAN) -> T?)) {
        this.success = success
    }

    fun onError(error: suspend ((e: Throwable) -> T?)) {
        this.error = error
    }

    internal fun onStart() {
        needLockView?.get()?.isEnabled = false
        loadingMsgLive?.postValue(loadingMsg)
        stateFlow?.tryEmit(RequestState.LOADING)
    }

    internal fun onComplete() {
//            wrap.stateListener?.postValue(RequestState.LOADED)
        loadingMsgLive?.postValue(null)
        needLockView?.get()?.isEnabled = true
        needLockView?.clear()
        needLockView = null
    }

    internal suspend fun onCatchingException(e: Exception): T? {
        stateFlow?.emit(RequestState.ERROR(e.message))
        return error.invoke(e)
    }
}

suspend fun <BEAN> coHttp(init: CoRequestWrap<BEAN, ProjectCoreHttpService, Unit>.() -> Unit): BEAN? {
    val wrap = CoRequestWrap<BEAN, ProjectCoreHttpService, Unit>()
    wrap.init()
    wrap.onStart()
    try {
        val response = wrap.apiSelect.invoke(ProjectCoreHttpManager.httpService!!, wrap.requestMap ?: RequestMap())
        if (response.isSuccessful) {
            val tBaseEntity = response.body() ?: throw ResultException("520", "tBaseEntity为null")
            try {
                wrap.success(tBaseEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return tBaseEntity
        } else {
            throw ResponseResultException(errCode = response.code().toString(), msg = response.message())
        }
    } catch (e: Exception) {
        wrap.onCatchingException(e)
        return null
    } finally {
        wrap.onComplete()
    }
}

suspend fun <BEAN> coHttpEither(init: CoRequestWrap<BEAN, ProjectCoreHttpService, Unit>.() -> Unit): Either {
    val wrap = CoRequestWrap<BEAN, ProjectCoreHttpService, Unit>()
    wrap.init()
    wrap.onStart()
    try {
        val response = wrap.apiSelect.invoke(ProjectCoreHttpManager.httpService!!, wrap.requestMap ?: RequestMap())
        if (response.isSuccessful) {
            val tBaseEntity = response.body() ?: throw ResultException("520", "tBaseEntity为null")
            try {
                wrap.success(tBaseEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Succ(tBaseEntity)
        } else {
            throw ResponseResultException(errCode = response.code().toString(), msg = response.message())
        }
    } catch (e: Exception) {
        wrap.onCatchingException(e)
        return Fail(e)
    } finally {
        wrap.onComplete()
    }
}

suspend fun <BEAN, RESULT> coHttpR(init: CoRequestWrap<BEAN, ProjectCoreHttpService, RESULT>.() -> Unit): RESULT? {
    val wrap = CoRequestWrap<BEAN, ProjectCoreHttpService, RESULT>()
    wrap.init()
    wrap.onStart()
    try {
        val response = wrap.apiSelect.invoke(ProjectCoreHttpManager.httpService!!, wrap.requestMap ?: RequestMap())
        if (response.isSuccessful) {
            val tBaseEntity = response.body() ?: throw ResultException("520", "tBaseEntity为null")
            return try {
                wrap.success(tBaseEntity)
            } catch (e: Exception) {
                e.printStackTrace()
                wrap.defaultT
            }
        } else {
            throw ResponseResultException(errCode = response.code().toString(), msg = response.message())
        }
    } catch (e: Exception) {
        return wrap.onCatchingException(e)
    } finally {
        wrap.onComplete()
    }
}

