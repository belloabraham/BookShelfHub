package com.bookshelfhub.bookshelfhub.helpers.utils

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import timber.log.Timber

object ColorUtil {

     fun get(context: Context, res: Int): Int {
        return try {
            ContextCompat.getColor(context, res)
        } catch (e: Resources.NotFoundException) {
            Timber.e(e)
            res
        }
    }
}