package com.bookshelfhub.bookshelfhub.extensions

import android.app.Activity
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.bookshelfhub.bookshelfhub.views.Toast

fun Activity.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}

fun Activity.isDarkMode(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO -> {
            false
        }
        else -> true
    }
}

fun Activity.enableDarkTheme(){
    val mode =  AppCompatDelegate.MODE_NIGHT_YES
    AppCompatDelegate.setDefaultNightMode(mode)
}

fun Activity.showToast(msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}


