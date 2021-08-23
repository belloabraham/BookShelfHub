package com.bookshelfhub.bookshelfhub.extensions

import android.app.Activity
import android.view.WindowManager
import com.bookshelfhub.bookshelfhub.views.Toast

fun Activity.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}

fun Activity.showToast(msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}

fun Activity.keepScreenOn(){
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Activity.clearKeepScreenOn(){
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
