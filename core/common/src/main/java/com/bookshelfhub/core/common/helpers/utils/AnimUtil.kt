package com.bookshelfhub.core.common.helpers.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.*
import android.view.animation.AnimationUtils

class AnimUtil (private val activity:Activity) {


     fun animate(v: View, animationDelay:Long, animRes:Int) {
         v.visibility = INVISIBLE
        val animDelay = animationDelay + 20
        Handler(Looper.getMainLooper()).postDelayed({
            v.visibility = VISIBLE
            v.startAnimation(AnimationUtils.loadAnimation(activity, animRes))
        }, animDelay)
    }

}