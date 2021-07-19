package com.bookshelfhub.bookshelfhub.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.bookshelfhub.bookshelfhub.enums.ClipBoardType


class ClipboardHelper(val context: Context) {

    fun copyToClipBoard(stringToCopy:String, label:String=ClipBoardType.TEXT.KEY){
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(label, stringToCopy)
        clipboardManager.setPrimaryClip(clipData)
    }

}