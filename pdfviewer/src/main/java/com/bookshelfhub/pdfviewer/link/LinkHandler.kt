package com.bookshelfhub.pdfviewer.link

import com.bookshelfhub.pdfviewer.model.LinkTapEvent

interface LinkHandler {
    /**
     * Called when link was tapped by user
     *
     * @param event current event
     */
    fun handleLinkEvent(event: LinkTapEvent?)
}