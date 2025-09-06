package com.daarba.flashalert.ui

import android.app.ActivityOptions
import com.daarba.flashalert.data.Language
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.daarba.flashalert.BaseActivity
import com.daarba.flashalert.MainActivity
import com.daarba.flashalert.R
import com.daarba.flashalert.adapters.LanguageAdapter
import com.daarba.flashalert.databinding.ActivityLangaugeBinding
import com.daarba.flashalert.helper.LocaleHelper
import com.daarba.flashalert.helper.SessionManager
import com.daarba.flashalert.ui.fragments.onbaording.OnBoardActivity
import java.util.*

class LangaugeActivity : BaseActivity() {

    private lateinit var binding: ActivityLangaugeBinding
    private lateinit var languages: List<Language>
    private var selectedLanguageCode: String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLangaugeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 👇 Only skip if first launch flag is false
        val isFirstLaunch = SessionManager.getBool(SessionManager.KEY_IS_FIRST_LAUNCH, true)
        if (!isFirstLaunch) {
            startActivity(Intent(this, OnBoardActivity::class.java))
            finish()
            return
        }


        languages = listOf(
            Language("English", R.drawable.english, "en"),
            Language("Arabic", R.drawable.arabic, "ar"),
            Language("Portuguese", R.drawable.portogal, "pt"),
            Language("German", R.drawable.german, "de"),
            Language("French", R.drawable.french, "fr"),
            Language("Urdu", R.drawable.urdu, "ur"),
            Language("Hindi", R.drawable.hindi, "hi")
        )

        binding.rvLanguageList.layoutManager = LinearLayoutManager(this)
        binding.rvLanguageList.adapter = LanguageAdapter(languages) { selectedLanguage ->
            selectedLanguageCode = selectedLanguage.localeCode
        }

        binding.llApplyButton.setOnClickListener {
            LocaleHelper.updateLocaleLanguage(this, selectedLanguageCode)
            SessionManager.putBool(SessionManager.KEY_IS_FIRST_LAUNCH, true)
            Toast.makeText(
                this,
                "Language changed successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, OnBoardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            startActivity(
                intent,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
        }
    }


    // Function to update locale based on selected language code
    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}
