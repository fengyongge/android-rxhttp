package com.fengyongge.rxhttp.exception

import android.net.ParseException
import com.fengyongge.rxhttp.exception.Code.Companion.BAD_GATEWAY
import com.fengyongge.rxhttp.exception.Code.Companion.FORBIDDEN
import com.fengyongge.rxhttp.exception.Code.Companion.GATEWAY_TIMEOUT
import com.fengyongge.rxhttp.exception.Code.Companion.HTTP_ERROR
import com.fengyongge.rxhttp.exception.Code.Companion.INTERNAL_SERVER_ERROR
import com.fengyongge.rxhttp.exception.Code.Companion.NET_ERROR
import com.fengyongge.rxhttp.exception.Code.Companion.NOT_FOUND
import com.fengyongge.rxhttp.exception.Code.Companion.NO_NET
import com.fengyongge.rxhttp.exception.Code.Companion.PARSE_ERROR
import com.fengyongge.rxhttp.exception.Code.Companion.REQUEST_TIMEOUT
import com.fengyongge.rxhttp.exception.Code.Companion.SERVICE_UNAVAILABLE
import com.fengyongge.rxhttp.exception.Code.Companion.SSL_ERROR
import com.fengyongge.rxhttp.exception.Code.Companion.TIMEOUT_ERROR
import com.fengyongge.rxhttp.exception.Code.Companion.UNAUTHORIZED
import com.fengyongge.rxhttp.exception.Code.Companion.UNKNOWN_ERROR
import com.fengyongge.rxhttp.core.RxHttp
import com.fengyongge.rxhttp.utils.NetUtils
import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * GitHub: https://github.com/fengyongge/android-rxhttp
 * @date 2020/09/08
 */
class ExceptionHandler {
    companion object {
        private lateinit var responseException: ResponseException

        fun handle(e: Throwable): ResponseException {

            if(!NetUtils.isConnected(RxHttp.getAppContext())){
                responseException = ResponseException(e, NO_NET, "网络连接失败，请检查网络设置")
                return responseException
            }

            if (e is ApiException) {
                responseException = ResponseException(e, Integer.valueOf(e.errorCode), e.message)
            } else if (e is HttpException) {
                responseException = when (e.code()) {
                    UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT, INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE -> ResponseException(e, "$HTTP_ERROR:${e.code()}", "网络连接错误")
                    else -> ResponseException(e, "$HTTP_ERROR:${e.code()}", "网络连接错误（${e.code()}）")
                }
            } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException) {
                responseException = ResponseException(e, PARSE_ERROR, "解析错误")
            } else if (e is ConnectException) {
                responseException = ResponseException(e, NET_ERROR, "连接失败，请稍后重试")
            } else if (e is ConnectTimeoutException || e is java.net.SocketTimeoutException) {
                responseException = ResponseException(e, TIMEOUT_ERROR, "网络连接超时，请稍后重试")
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                responseException = ResponseException(e, SSL_ERROR, "证书验证失败")
            } else {
                responseException = ResponseException(e, UNKNOWN_ERROR, "未知错误，请稍后重试")
            }
            return responseException
        }
    }


}