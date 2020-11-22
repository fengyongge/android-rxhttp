package com.fengyongge.rxhttpsample.login

import com.fengyongge.rxhttp.bean.BaseResponse
import com.fengyongge.rxhttpsample.bean.LoginBean
import com.fengyongge.rxhttpsample.net.MyRetrofit
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginModelImple : LoginContract.model {
    override fun postLogin(userName: String, password: String): Observable<BaseResponse<LoginBean>> {
        return MyRetrofit.service.login(userName,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}