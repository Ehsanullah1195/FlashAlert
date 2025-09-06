package com.daarba.flashalert.ui.fragments.onbaording

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.daarba.flashalert.BaseActivity
import com.daarba.flashalert.MainActivity
import com.daarba.flashalert.R
import com.daarba.flashalert.databinding.ActivityOnBoardBinding
import com.daarba.flashalert.helper.AppController

class OnBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityOnBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(binding.clRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layouts = intArrayOf(
            R.layout.onboarding_screen1,
            R.layout.onboarding_screen2,
            R.layout.onboardin_screen3
        )

        val adapter = OnboardAdapter(this, layouts)
        binding.viewPager.adapter = adapter
        binding.btnGetStarted.text = getString(R.string.next)

        // Background Change & Button Text Update
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val background = when (position) {
                    0 -> R.drawable.onboard1_background
                    1 -> R.drawable.onboard1_background
                    2 -> R.drawable.onboard_screen3_background
                    else -> R.drawable.onboard1_background
                }
                if (position == layouts.size - 1) {
                    binding.btnGetStarted.text = getString(R.string.get_started)
                } else {
                    binding.btnGetStarted.text = getString(R.string.next)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        // "Next" Button Click Listener
        binding.btnGetStarted.setOnClickListener {
            val current = binding.viewPager.currentItem
            val nextItem = current + 1

            if (nextItem < layouts.size) {
                binding.viewPager.currentItem = nextItem
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // "Skip" Button Click Listener
        binding.btnSkip.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
