package com.bookshelfhub.bookshelfhub.extensions

import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.views.Toast

fun Fragment.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this.requireActivity()).showToast(msg, toastLength)
}

fun Fragment.showToast(msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    Toast(this.requireActivity()).showToast(msg, toastLength)
}