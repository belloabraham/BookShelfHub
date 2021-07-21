package com.bookshelfhub.pdfviewer.listener

interface OnPageChangeListener {

    /**
     * Called when the user use swipe to change page
     *
     * @param page      the new page displayed, starting from 0
     * @param pageCount the total page count
     */
    fun onPageChanged(page: Int, pageCount: Int)
}