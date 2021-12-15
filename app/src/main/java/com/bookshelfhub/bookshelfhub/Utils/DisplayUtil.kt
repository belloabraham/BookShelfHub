package com.bookshelfhub.bookshelfhub.Utils

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
        if(dp==0f){
            return 0f
        }
        return dp * context.resources.displayMetrics.density
    }
}