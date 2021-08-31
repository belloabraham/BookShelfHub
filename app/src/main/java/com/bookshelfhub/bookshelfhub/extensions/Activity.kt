package com.bookshelfhub.bookshelfhub.extensions

import android.app.Activity
import com.bookshelfhub.bookshelfhub.views.Toast

fun Activity.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}

fun Activity.showToast(msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this).showToast(msg, toastLength)
}


