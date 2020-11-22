package com.fengyongge.rxhttpsample.login

import com.fengyongge.baseframework.mvp.BasePresenter
import com.fengyongge.baseframework.mvp.IBaseView
import com.fengyongge.rxhttp.bean.BaseResponse
import com.fengyongge.rxhttp.core.BaseObserver
import com.fengyongge.rxhttp.exception.ResponseException
import com.fengyongge.rxhttpsample.bean.LoginBean

class LoginPresenterImpl : BasePresenter<LoginContract.view>(),LoginContract.presenter {

     var loginModelImple: LoginModelImple ?=null

    override fun postLogin(userName: String, password: String) {

        mView?.getCurrentView().let {

            loginModelImple?.postLogin(userName,password)?.subscribe(object : BaseObserver<BaseResponse<LoginBean>>(){
                override fun onSuccess(data: BaseResponse<LoginBean>) {
                    mView?.loginSucccess(data)
                }

                override fun onError(e: ResponseException) {
                    mView?.loginFail(e)
                }
            })
        }
    }

    override fun attach(V: IBaseView) {
        super.attach(V)
        loginModelImple = LoginModelImple()
    }

    override fun detech() {
        super.detech()
        loginModelImple = null
    }
}