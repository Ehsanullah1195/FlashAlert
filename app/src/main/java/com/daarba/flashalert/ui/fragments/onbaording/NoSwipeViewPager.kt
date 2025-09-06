package com.daarba.flashalert.ui.fragments.onbaording

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class NoSwipeViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    override fun onTouchEvent(ev: android.view.MotionEvent?): Boolean {
        // Disable swipe
        return true
    }

    override fun onInterceptTouchEvent(ev: android.view.MotionEvent?): Boolean {
        // Disable swipe
        return true

    }
}
