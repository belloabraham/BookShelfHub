package com.bookshelfhub.bookshelfhub.helpers.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class ClipboardHelper(val context: Context) {

    fun copyToClipBoard(stringToCopy:String, label:String= ClipBoardType.TEXT.VALUE){
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(label, stringToCopy)
        clipboardManager.setPrimaryClip(clipData)
    }
}