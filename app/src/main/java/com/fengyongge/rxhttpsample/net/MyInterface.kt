package com.fengyongge.rxhttpsample.net

import com.fengyongge.rxhttp.bean.BaseResponse
import com.fengyongge.rxhttpsample.bean.LoginBean
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyInterface {

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username")username: String, @Field("password")password: String): Observable<BaseResponse<LoginBean>>

}