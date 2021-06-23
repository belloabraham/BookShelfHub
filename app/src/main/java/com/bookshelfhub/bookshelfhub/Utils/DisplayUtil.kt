package com.bookshelfhub.bookshelfhub.Utils

import android.util.DisplayMetrics
import android.content.Context
import android.content.res.Resources



class DisplayUtil {

    companion object{
        fun convertPixelsToDp(px: Float, context: Context): Float {
            val resources: Resources = context.getResources()
            val metrics: DisplayMetrics = resources.getDisplayMetrics()
            return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun convertDpToPixels(context: Context, dp: Float): Float {
            return dp * context.getResources().getDisplayMetrics().density
        }
    }

}