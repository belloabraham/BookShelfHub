package com.bookshelfhub.bookshelfhub.Utils

import android.app.Activity
import android.content.Intent

class Share(private val activity:Activity) {


    fun shareText(text:String){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
    }

}