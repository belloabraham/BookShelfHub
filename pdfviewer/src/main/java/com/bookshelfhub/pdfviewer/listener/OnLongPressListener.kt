package com.bookshelfhub.pdfviewer.listener

import android.view.MotionEvent

interface OnLongPressListener {

    /**
     * Called when the user has a long tap gesture, before processing scroll handle toggling
     *
     * @param e MotionEvent that registered as a confirmed long press
     */
    fun onLongPress(e: MotionEvent?)

}