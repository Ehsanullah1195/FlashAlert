package com.daarba.flashalert.helper

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.Locale

object LocaleHelper {

    fun updateLocaleLanguage(context: Context, localeLangCode: String) {
        SessionManager.setString(SessionManager.KEY_LOCALE_LANGUAGE, localeLangCode)
        // Only update system locale manager for API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(localeLangCode)
        }
    }

    fun wrap(context: Context): Context {
        val lang = SessionManager.getString(SessionManager.KEY_LOCALE_LANGUAGE, "en")
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

}