package com.fengyongge.baseframework

import android.app.Activity
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

/**
 * describe
 *
 * @author fengyongge(fengyongge98@gmail.com)
 * @version V1.0
 * @date 2020/09/08
 */
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mContext: BaseActivity
    var activitys = mutableListOf<Activity>()


    @LayoutRes
    abstract fun initLayout(): Int
    abstract fun initView()
    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayout())
        mContext = this
        initData()
        initView()
    }

    override fun onResume() {
        super.onResume()
        activitys?.let {
            it.add(this)
        }
    }
    override fun onPause() {
        super.onPause()
    }

    open fun exitApp() {
        if (activitys != null) {
            for (activity in activitys) {
                activity?.finish()
            }
        }
        exitProcess(0)
    }



}