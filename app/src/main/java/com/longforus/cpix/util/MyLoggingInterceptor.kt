package com.longforus.cpix.util

import android.util.Log
import okhttp3.*
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.BufferedSink
import okio.ByteString.Companion.encodeUtf8
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 2017/6/16  10:55.
 * Description : 删除冗余输出信息和url输出定制
 */

class MyLoggingInterceptor @JvmOverloads constructor(private val logger: Logger = Logger.DEFAULT) : Interceptor {
    @Volatile
    private var level: Level? = null

    init {
        this.level = Level.NONE
    }

    fun getLevel(): Level? {
        return this.level
    }

    fun setLevel(level: Level?): MyLoggingInterceptor {
        if (level == null) {
            throw NullPointerException("level == null. Use Level.NONE instead.")
        } else {
            this.level = level
            return this
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        } else {
            val logBody = level == Level.BODY
            val logHeaders = logBody || level == Level.HEADERS
            val requestBody = request.body()
            val hasRequestBody = requestBody != null
            val connection = chain.connection()
            val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
            var requestStartMessage = "--> " + request.method() + ' '.toString() + request.url() + ' '.toString() + protocol
            if (!logHeaders && hasRequestBody) {
                requestStartMessage = requestStartMessage + " (" + requestBody!!.contentLength() + "-byte body)"
            }
            var bodyStr = ""
            this.logger.log(requestStartMessage)
            if (logHeaders) {
                if (hasRequestBody) {
                    if (requestBody!!.contentType() != null) {
                        this.logger.log("Content-Type: " + requestBody.contentType()!!)
                    }

                    if (requestBody.contentLength() != -1L) {
                        this.logger.log("Content-Length: " + requestBody.contentLength())
                    }
                }

                val startNs = request.headers()
                var buffer = 0

                val response = startNs.size()
                while (buffer < response) {
                    val tookMs = startNs.name(buffer)
                    if (!"Content-Type".equals(tookMs, ignoreCase = true) && !"Content-Length".equals(tookMs, ignoreCase = true)) {
                        this.logger.log(tookMs + ": " + startNs.value(buffer))
                    }
                    ++buffer
                }
                if (logBody && hasRequestBody) {
                    if (this.bodyEncoded(request.headers())) {
                        this.logger.log("--> END " + request.method() + " (encoded body omitted)")
                    } else {
                        val buf = Buffer()
                        if (requestBody is MultipartBody) {
                            writeOrCountBytes(requestBody, buf, false)
                            bodyStr = buf.readString(UTF8)
//                            val decode = URLDecoder.decode(bodyStr, "UTF-8")
//                            val sb = StringBuilder("request Param : ")
//                            sb.append("\n               ")
//                            sb.append(decode)
//                            this.logger.log(Log.WARN, sb.toString())
                        } else {
                            requestBody!!.writeTo(buf)
                            try {
                                var var29: Charset? = UTF8
                                val var31 = requestBody.contentType()
                                if (var31 != null) {
                                    var29 = var31.charset(UTF8)
                                }
                                bodyStr = buf.readString(var29!!)
                                val decode = URLDecoder.decode(bodyStr, "UTF-8")
                                val split = decode.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                val sb = StringBuilder("request Param : ")
                                if (split.isNotEmpty()) {
                                    for (s in split) {
                                        sb.append("\n               ")
                                        sb.append(s.ifTakeAppendLength(1000))
                                    }
                                } else {
                                    sb.append("\n               ")
                                    sb.append(decode.ifTakeAppendLength(1000))
                                }
                                this.logger.log(Log.WARN, sb.toString())
                            } catch (e: Exception) {
                            }
                        }
                        this.logger.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)")
                    }
                } else {
                    this.logger.log("--> END " + request.method())
                }
            }

            val var27 = System.nanoTime()

            val old = chain.proceed(request)
            val var32 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var27)
            val responseBody = old.body()
            val contentLength = responseBody!!.contentLength()
            val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
            this.logger.log(
                "<-- " + old.code() + ' '.toString() + old.message() + ' '.toString() + old.request().url() + '?'.toString() + bodyStr.ifTakeAppendLength(
                    3000) + " (" + var32 + "ms" + (if (!logHeaders) ", $bodySize body" else "") + ')'.toString())
            if (bodyStr.contains("%")) {
                try {
                    this.logger.log(Log.WARN, "decode url  =  \n             " + old.request().url() + '?'.toString() + URLDecoder.decode(bodyStr.ifTakeAppendLength(3000), "UTF-8"))
                } catch (e: Exception) {
                }
            }
            if (logHeaders) {
                val headers = old.headers()
                var source = 0

                val buffer1 = headers.size()
                while (source < buffer1) {
                    this.logger.log(headers.name(source) + ": " + headers.value(source))
                    ++source
                }

                if (logBody && old.body() != null && false) {//返回的body也不要了
                    if (this.bodyEncoded(old.headers())) {
                        this.logger.log("<-- END HTTP (encoded body omitted)")
                    } else {
                        val var33 = responseBody.source()
                        var33.request(9223372036854775807L)
                        val var34 = var33.buffer()
                        var charset: Charset? = UTF8
                        val contentType = responseBody.contentType()
                        if (contentType != null) {
                            try {
                                charset = contentType.charset(UTF8)
                            } catch (var26: UnsupportedCharsetException) {
                                this.logger.log("Couldn\'t decode the response body; charset is likely malformed.")
                                this.logger.log("<-- END HTTP")
                                return old
                            }

                        }

                        if (contentLength != 0L) {
                            val string = var34.clone().readString(charset!!)
                            this.logger.log(string)
                        }

                        this.logger.log("<-- END HTTP (" + var34.size + "-byte body)")
                    }
                } else {
                    this.logger.log("<-- END HTTP")
                }
            }

            return old
        }
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !"identity".equals(contentEncoding, ignoreCase = true)
    }

    /**
     * Either writes this request to `sink` or measures its content length. We have one method
     * do double-duty to make sure the counting and content are consistent, particularly when it comes
     * to awkward operations like measuring the encoded length of header strings, or the
     * length-in-digits of an encoded integer.
     */
    @Throws(IOException::class)
    private fun writeOrCountBytes(mBody: MultipartBody, sink: BufferedSink?, countBytes: Boolean): Long {
        var sink = sink
        var byteCount = 0L

        var byteCountBuffer: Buffer? = null
        if (countBytes) {
            byteCountBuffer = Buffer()
            sink = byteCountBuffer
        }

        var p = 0

        val partCount = mBody.parts().size
        val byteString = mBody.boundary().encodeUtf8()
        while (p < partCount) {
            val part = mBody.parts().get(p)
            val headers = part.headers()
            val body = part.body()

//            sink!!.write(DASHDASH)
//            sink.write(byteString)
            sink!!.write(CRLF)

            if (headers != null) {
                var h = 0
                val headerCount = headers!!.size()
                while (h < headerCount) {
                    sink.writeUtf8(headers!!.name(h))
                        .write(COLONSPACE)
                        .writeUtf8(headers!!.value(h))
                        .write(CRLF)
                    h++
                }
            }

            var skipBody = false
            val contentType = body.contentType()
            if (contentType != null) {
                sink.writeUtf8("Content-Type: ")
                    .writeUtf8(contentType!!.toString())
                    .write(CRLF)
                skipBody = contentType == MediaType.parse("image/png") || contentType == MediaType.parse("audio/amr") || contentType == MediaType.parse("image/jpeg")
            }

            val contentLength = body.contentLength()
            if (contentLength != -1L) {
                sink.writeUtf8("Content-Length: ")
                    .writeDecimalLong(contentLength)
                    .write(CRLF)
            } else if (countBytes) {
                // We can't measure the body's size without the sizes of its components.
                byteCountBuffer!!.clear()
                return -1L
            }

            sink.write(CRLF)


            if (countBytes || skipBody) {
                byteCount += contentLength
            } else {
                sink.write("body  =  ".toByteArray())
                body.writeTo(sink)
            }

            sink.write(CRLF)
            sink.write("------------------- (∩•̀ω•́)⊃-*妖孽，看我不收了你！ ---------------".toByteArray())
            sink.write(CRLF)
            p++
        }

        sink!!.write(DASHDASH)
        sink.write(byteString)
        sink.write(DASHDASH)
        sink.write(CRLF)

        if (countBytes) {
            byteCount += byteCountBuffer!!.size
            byteCountBuffer.clear()
        }

        return byteCount
    }

    enum class Level private constructor() {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    interface Logger {

        fun log(var1: String)

        fun log(level: Int, var1: String)

        companion object {
            val DEFAULT: Logger = object : Logger {
                override fun log(message: String) {
                    Platform.get().log(Log.DEBUG, message, null)
                }

                override fun log(level: Int, var1: String) {
                    Platform.get().log(level, var1, null)
                }
            }
        }
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        var COLONSPACE = byteArrayOf(':'.toByte(), ' '.toByte())
        var CRLF = byteArrayOf('\r'.toByte(), '\n'.toByte())
        var DASHDASH = byteArrayOf('-'.toByte(), '-'.toByte())
    }
}
