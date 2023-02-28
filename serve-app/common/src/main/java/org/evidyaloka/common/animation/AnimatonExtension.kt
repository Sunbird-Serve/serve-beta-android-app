package org.evidyaloka.common.animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */

    fun View.slideOutAnimation(currentHeight: Int = this.height, newHeight: Int=0)=
    run {
        val slideAnimator = ValueAnimator
            .ofInt(currentHeight, newHeight)
            .setDuration(500)
        slideAnimator.addUpdateListener { animation1: ValueAnimator ->
            val value = animation1.animatedValue as Int
            this.layoutParams.height = value
            this.requestLayout()
        }
        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator();
        animationSet.play(slideAnimator);
        animationSet.start()
    }

fun View.slideInAnimation(currentHeight: Int = 0, newHeight: Int)=
    run {
        val slideAnimator = ValueAnimator
            .ofInt(currentHeight, newHeight)
            .setDuration(500)
        slideAnimator.addUpdateListener { animation1: ValueAnimator ->
            val value = animation1.animatedValue as Int
            this.layoutParams.height = value
            this.requestLayout()
        }
        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator();
        animationSet.play(slideAnimator);
        animationSet.start()
    }
