package com.fengyongge.rxhttpsample.login

import android.widget.Button
import android.widget.Toast
import com.fengyongge.baseframework.mvp.BaseMvpActivity
import com.fengyongge.rxhttp.bean.BaseResponse
import com.fengyongge.rxhttp.exception.ResponseException
import com.fengyongge.rxhttpsample.R
import com.fengyongge.rxhttpsample.bean.LoginBean

class LoginActivity : BaseMvpActivity<LoginPresenterImpl>(), LoginContract.view {
    lateinit var button: Button

    override fun initLayout(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            mPresenter?.postLogin("fwanandroid", "")
        }

    }

    override fun initData() {

    }

    override fun initPresenter(): LoginPresenterImpl {
        return LoginPresenterImpl()
    }

    override fun loginSucccess(data: BaseResponse<LoginBean>) {


        if (data.errorCode == "0") {
            button.text = data.data.nickname
        }


    }

    override fun loginFail(e: ResponseException) {

        Toast.makeText(this, e.getErrorMessage(), Toast.LENGTH_SHORT).show()

    }
}