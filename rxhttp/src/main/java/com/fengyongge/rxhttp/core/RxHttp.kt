package com.fengyongge.rxhttp.core

import android.content.Context

/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * GitHub: https://github.com/fengyongge/android-rxhttp
 * @date 2020/09/08
 */
class RxHttp(context: Context) {

    companion object {
        private var instance:RxHttp?= null
        private lateinit var context:Context

        fun init(context: Context):RxHttp{
            this.context = context
            instance = RxHttp(context)
            return instance!!
        }

        fun getAppContext(): Context{
            return context
        }
    }

}