package org.evidyaloka.common.ui.rtc

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ForegroundToBackgroundPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val height = page.height.toFloat()
        val width = page.width.toFloat()
        val scale = Math.min(if (pos > 0) 1f else Math.abs(1f + pos), 1f)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = width * 0.5f
        page.pivotY = height * 0.5f
        page.translationX = if (pos > 0) width * pos else -width * pos * 0.25f
    }

}