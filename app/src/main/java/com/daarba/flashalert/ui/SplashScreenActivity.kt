package com.daarba.flashalert.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daarba.flashalert.BaseActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.daarba.flashalert.R
import com.daarba.flashalert.helper.AppController

class SplashScreenActivity : BaseActivity() {

    private lateinit var progressBar: LinearProgressIndicator
    private val progressMax = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        progressBar = findViewById(R.id.splash_progress_bar)
        progressBar.setIndicatorColor(getColor(R.color.text_yellow))
        progressBar.max = progressMax
        hideSystemUI()
        startProgressUpdate()
    }

    private fun hideSystemUI() {
        window.decorView.apply {
            systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun startProgressUpdate() {
        val handler = Handler(Looper.getMainLooper())
        var progress = 0

        val totalDuration = 5000L
        val updateInterval = 50L
        val incrementValue = (progressMax * updateInterval / totalDuration).toInt()

        val runnable = object : Runnable {
            override fun run() {
                progressBar.progress = progress
                if (progress >= progressMax) {
                    startActivity(Intent(this@SplashScreenActivity, LangaugeActivity::class.java))
                    finish()
                } else {
                    progress += incrementValue
                    handler.postDelayed(this, updateInterval)
                }
            }
        }
        handler.post(runnable)
    }

}
