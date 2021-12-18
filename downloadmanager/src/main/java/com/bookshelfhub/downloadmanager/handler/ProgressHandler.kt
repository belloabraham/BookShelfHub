package com.bookshelfhub.downloadmanager.handler

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.bookshelfhub.downloadmanager.Constants
import com.bookshelfhub.downloadmanager.OnProgressListener
import com.bookshelfhub.downloadmanager.Progress

class ProgressHandler(private val listener: OnProgressListener?):Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            Constants.UPDATE -> if (listener != null) {
                val progress = msg.obj as Progress
                listener.onProgress(progress)
            }
            else -> super.handleMessage(msg)
        }
    }

}