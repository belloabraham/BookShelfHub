package com.bookshelfhub.bookshelfhub.helpers.utils

import android.util.DisplayMetrics
import android.content.Context
import android.content.res.Resources


object DisplayUtil {

    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources: Resources = context.resources
        val metrics: DisplayMetrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertDpToPixels(context: Context, dp: Float): Float {
       return if(dp==0f){
             0f
       }else {
           dp * context.resources.displayMetrics.density
       }
    }
}