package com.daarba.flashalert.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.daarba.flashalert.R
import com.daarba.flashalert.databinding.FragmentScreenLightBinding

class ScreenLightFragment : Fragment() {

    private lateinit var binding: FragmentScreenLightBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScreenLightBinding.inflate(inflater, container, false)

        //hideSystemUI()
        hideBottomNavigation()

        binding.screenBackground.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val color = getColorFromPosition(event.x, event.y)
                binding.screenBackground.setBackgroundColor(color)
            }
            true
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation()
        showSystemUI()
    }


        private fun showSystemUI() {
            requireActivity().window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_VISIBLE
                    )
        }

    private fun getColorFromPosition(x: Float, y: Float): Int {
        val width = binding.screenBackground.width.toFloat()
        val height = binding.screenBackground.height.toFloat()

        if (width == 0f || height == 0f) return Color.BLACK

        val normalizedX = (x / width).coerceIn(0f, 1f)
        val normalizedY = (y / height).coerceIn(0f, 1f)

        val red = (255 * (1 - normalizedX)).toInt()
        val green = (255 * normalizedY).toInt()
        val blue = (255 * normalizedX).toInt()

        return Color.rgb(red, green, blue)
    }

    private fun hideSystemUI() {
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        //(requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    private fun hideBottomNavigation() {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE
    }

}
