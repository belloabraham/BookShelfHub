package com.bookshelfhub.bookshelfhub.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class ClipboardHelper(val context: Context) {

    fun copyToClipBoard(value:String){
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", value)
        clipboardManager.setPrimaryClip(clipData)
    }
}