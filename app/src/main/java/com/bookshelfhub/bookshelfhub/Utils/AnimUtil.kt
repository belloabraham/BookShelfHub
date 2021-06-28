package com.bookshelfhub.bookshelfhub.Utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils


class AnimUtil (private val activity:Activity) {


     fun animate(v: View, animationDelay:Long, animRes:Int) {
        v.setVisibility(INVISIBLE)
        val animDelay = animationDelay + 20
        Handler().postDelayed(Runnable {
            v.setVisibility(VISIBLE)
            v.startAnimation(AnimationUtils.loadAnimation(activity, animRes))
        }, animDelay)
    }

}