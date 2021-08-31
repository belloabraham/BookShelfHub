package com.bookshelfhub.bookshelfhub.helpers.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class ClipboardHelper(val context: Context) {

    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


    fun copyToClipBoard(stringToCopy:String, label:String = ClipBoardType.TEXT.VALUE){
        val clipData = ClipData.newPlainText(label, stringToCopy)
        clipboardManager.setPrimaryClip(clipData)
    }
}