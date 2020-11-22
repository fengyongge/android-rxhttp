package com.fengyongge.rxhttpsample.login

import com.fengyongge.baseframework.mvp.IBasePresenter
import com.fengyongge.baseframework.mvp.IBaseView
import com.fengyongge.rxhttp.bean.BaseResponse
import com.fengyongge.rxhttp.exception.ResponseException
import com.fengyongge.rxhttpsample.bean.LoginBean
import io.reactivex.Observable

class LoginContract {

    interface presenter : IBasePresenter{

        fun postLogin(userName: String,password: String)

    }

    interface view : IBaseView{

        fun loginSucccess(data: BaseResponse<LoginBean>)

        fun loginFail(e: ResponseException)

    }

    interface model{

        fun postLogin(userName: String,password: String) : Observable<BaseResponse<LoginBean>>

    }



}