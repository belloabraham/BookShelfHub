package com.bookshelfhub.bookshelfhub.Utils

import android.util.DisplayMetrics
import android.content.Context
import android.content.res.Resources



class DisplayUtil {

    companion object{
        fun convertPixelsToDp(px: Float, context: Context): Float {
            val resources: Resources = context.resources
            val metrics: DisplayMetrics = resources.displayMetrics
            return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun convertDpToPixels(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }
    }

}