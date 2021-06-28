package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.content.res.Resources
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat

class ColorUtil {
    
    companion object{
        operator fun get(context: Context, res: Int): Int {
            return try {
                ContextCompat.getColor(context, res)
            } catch (e: Resources.NotFoundException) {
                res
            }
        }
    }
}