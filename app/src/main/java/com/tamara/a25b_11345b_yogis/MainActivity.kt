package com.tamara.a25b_11345b_yogis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tamara.a25b_11345b_yogis.ui.auth.SplashFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SplashFragment())
            .commit()
    }
}
