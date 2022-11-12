package com.bookshelfhub.core.common.extensions

import android.app.Activity
import android.content.res.Configuration
import androidx.annotation.StringRes
import com.bookshelfhub.core.common.helpers.utils.Toast

fun Activity.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}

fun Activity.isAppUsingDarkTheme(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO ->
            false
        else -> true
    }
}

fun Activity.showToast(@StringRes msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}