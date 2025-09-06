package com.daarba.flashalert.ui.fragments.onbaording

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class OnboardAdapter(private val context: Context, private val layouts: IntArray) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layouts[position], container, false)
        view.tag = "screen_$position"
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = layouts.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
}
