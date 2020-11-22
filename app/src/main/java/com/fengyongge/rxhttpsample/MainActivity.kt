package com.fengyongge.rxhttpsample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fengyongge.rxhttpsample.login.LoginActivity

/**
 * Created by fengyongge on 16/5/12.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, LoginActivity::class.java))

    }

}


