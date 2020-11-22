package com.fengyongge.rxhttpsample.net

import com.fengyongge.rxhttp.core.RetrofitFactory
import okhttp3.Interceptor

object MyRetrofit : RetrofitFactory<MyInterface>() {
    override fun baseUrl(): String {
        return "https://www.wanandroid.com/"
    }

    override fun getService(): Class<MyInterface> {
        return MyInterface::class.java
    }

    override fun getInterceptorList(): MutableList<Interceptor> {
        var interceptorList = mutableListOf<Interceptor>()
        interceptorList.add(AddCookiesInterceptor());
        return interceptorList;

    }
}