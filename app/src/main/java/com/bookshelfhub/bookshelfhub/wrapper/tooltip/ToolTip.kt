package com.bookshelfhub.bookshelfhub.wrapper.tooltip

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LifecycleOwner

class ToolTip(context: Context) : SkyDove(context) {


    override fun showPhoneNumErrorBottom(
        anchorView: View,
        message: String,
        editText: AppCompatEditText,
        event: (AppCompatEditText) -> Unit
    ) {
        super.showPhoneNumErrorBottom(anchorView, message, editText, event)
    }
}