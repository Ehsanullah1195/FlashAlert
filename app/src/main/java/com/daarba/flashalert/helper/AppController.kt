package com.daarba.flashalert.helper

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class AppController: Application() {

    override fun onCreate() {
        super.onCreate()
        SessionManager.with(this)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}