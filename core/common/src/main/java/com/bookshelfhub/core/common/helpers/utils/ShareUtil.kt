package com.bookshelfhub.core.common.helpers.utils

import android.content.Intent

object ShareUtil {

    fun getShareIntent(text:String, title:String): Intent? {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        return  Intent.createChooser(sendIntent, title)
    }

}