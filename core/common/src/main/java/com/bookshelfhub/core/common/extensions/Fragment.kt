package com.bookshelfhub.core.common.extensions

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.bookshelfhub.core.common.helpers.utils.Toast
import com.bookshelfhub.core.common.helpers.utils.ToolTip

fun Fragment.showToast(msg:String, toastLength:Int = Toast.LENGTH_LONG){
    requireActivity().showToast(msg, toastLength)
}

fun Fragment.isAppUsingDarkTheme(): Boolean {
    return requireActivity().isAppUsingDarkTheme()
}

fun Fragment.showToast(@StringRes msg:Int, toastLength:Int = Toast.LENGTH_LONG){
    requireActivity().showToast(msg, toastLength)
}

fun Fragment.showErrorToolTip(anchorView: View, message:String, event: ()->Unit ){
    val balloon = ToolTip(requireContext()).showPhoneNumberError(message, event)
    balloon.showAlignBottom(anchorView)
}
