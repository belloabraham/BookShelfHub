package com.bookshelfhub.pdfviewer.listener

interface OnLoadCompleteListener {

    /**
     * Called when the PDF is loaded
     * @param nbPages the number of pages in this PDF file
     */
    fun loadComplete(nbPages: Int)

}