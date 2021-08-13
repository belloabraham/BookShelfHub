package com.bookshelfhub.bookshelfhub.Utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import java.time.Duration


class AnimUtil (private val activity:Activity) {


     fun animate(v: View, animationDelay:Long, animRes:Int) {
         v.visibility = INVISIBLE
        val animDelay = animationDelay + 20
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            v.visibility = VISIBLE
            v.startAnimation(AnimationUtils.loadAnimation(activity, animRes))
        }, animDelay)
    }

    fun fadeIn(view: View, duration: Long){
        view.apply {
            alpha =0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null)
        }
    }

    fun crossFade(invisibleView: View, visibleView: View, duration:Long){
        invisibleView.apply {
            alpha =0f
            visibility = VISIBLE
            animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null)
        }
        visibleView.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    visibleView.visibility = GONE
                }
            })
    }
}