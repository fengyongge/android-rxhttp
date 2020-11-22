package com.fengyongge.rxhttpsample.net

import okhttp3.Interceptor
import okhttp3.Response

/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * @version V1.0
 * @date 2020/09/08
 */
class AddCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = chain.request().newBuilder()

        return chain.proceed(builder.build())
    }
}