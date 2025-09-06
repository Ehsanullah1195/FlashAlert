package com.daarba.flashalert.ui.fragments.setting

import android.app.ActivityOptions
import com.daarba.flashalert.data.Language
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daarba.flashalert.MainActivity
import com.daarba.flashalert.R
import com.daarba.flashalert.adapters.SettingLangaugeAdapter
import com.daarba.flashalert.databinding.ActivitySettingLanguageBinding
import com.daarba.flashalert.helper.LocaleHelper
import com.daarba.flashalert.helper.SessionManager

class LanguageFragment: Fragment() {

    private lateinit var binding:ActivitySettingLanguageBinding
    private lateinit var toolbar: Toolbar
    private var selectedLanguageCode = SessionManager.getString(SessionManager.KEY_LOCALE_LANGUAGE, "en")
    private val applyButton by lazy {
        ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_apply) // your icon
            setPadding(32, 16, 32, 16)
            layoutParams = Toolbar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END
            )
            setOnClickListener {
                applyLanguage(selectedLanguageCode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivitySettingLanguageBinding.inflate(inflater, container, false)
        toolbar = (requireActivity() as AppCompatActivity).findViewById(R.id.toolbar)
        toolbar.addView(applyButton)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages = listOf(
            Language("English", R.drawable.english, "en"),
            Language("Arabic", R.drawable.arabic, "ar"),
            Language("Spanish", R.drawable.spanish_icone, "es"),
            Language("Portuguese", R.drawable.portogal, "pt"),
            Language("German", R.drawable.german, "de"),
            Language("French", R.drawable.french, "fr"),
            Language("Urdu", R.drawable.urdu, "ur"),
            Language("Hindi", R.drawable.hindi, "hi")
        )

        // Setup RecyclerView to display languages
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.languageRecyclerView.adapter = SettingLangaugeAdapter(languages) { selectedLanguage ->
            selectedLanguageCode = selectedLanguage.localeCode
            binding.llApplyButton.visibility = View.VISIBLE
        }

        binding.llApplyButton.setOnClickListener {
            applyLanguage(selectedLanguageCode)
        }

        binding.imgSkip.setOnClickListener {
            applyLanguage(selectedLanguageCode)
        }
    }

    private fun applyLanguage(languageCode: String) {
        LocaleHelper.updateLocaleLanguage(requireContext(), languageCode)
        Toast.makeText(
            requireContext(),
            "Language changed successfully",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(
                requireContext(),
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
        )
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar.removeView(applyButton)
    }

}