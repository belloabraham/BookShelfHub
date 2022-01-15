package com.bookshelfhub.bookshelfhub.Utils

import android.app.Activity
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