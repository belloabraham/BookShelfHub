package com.bookshelfhub.bookshelfhub.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.views.Toast
import com.bookshelfhub.bookshelfhub.views.ToolTip

fun Fragment.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    requireActivity().showToast(msg, toastLength)
}

fun Fragment.isDarkMode(): Boolean {
    return requireActivity().isDarkMode()
}

fun Fragment.showToast(msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    requireActivity().showToast(msg, toastLength)
}

fun Fragment.showErrorToolTip(anchorView: View, message:String, event: ()->Unit ){
    val balloon = ToolTip(requireContext()).showPhoneNumberError(message, event)
    balloon.showAlignBottom(anchorView)
}
