package com.bookshelfhub.pdfviewer.model


import android.graphics.RectF

import com.shockwave.pdfium.PdfDocument

class LinkTapEvent(private val originalX: Float,
                   private val originalY: Float,
                   private val documentX: Float,
                   private val documentY: Float,
                   private val mappedLinkRect: RectF?,
                   private val link: PdfDocument.Link?) {

    fun getOriginalX(): Float {
        return originalX
    }

    fun getOriginalY(): Float {
        return originalY
    }

    fun getDocumentX(): Float {
        return documentX
    }

    fun getDocumentY(): Float {
        return documentY
    }

    fun getMappedLinkRect(): RectF? {
        return mappedLinkRect
    }

    fun getLink(): PdfDocument.Link? {
        return link
    }

}


