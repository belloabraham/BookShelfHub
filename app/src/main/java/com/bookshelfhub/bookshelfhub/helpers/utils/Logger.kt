package com.bookshelfhub.bookshelfhub.helpers.utils

import android.util.Log
import androidx.annotation.Size

object Logger {

    fun log(@Size(max=23) tag:String, e:Exception){
        Log.d(tag, e.message.toString())
    }
}