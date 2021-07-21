package com.bookshelfhub.pdfviewer.link


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.bookshelfhub.pdfviewer.PDFView
import com.bookshelfhub.pdfviewer.model.LinkTapEvent

class DefaultLinkHandler(private val pdfView: PDFView) : LinkHandler {


    private val TAG = DefaultLinkHandler::class.java.simpleName


    override fun handleLinkEvent(event: LinkTapEvent?) {
        val uri: String? = event?.getLink()?.getUri()
        val page: Int? = event?.getLink()?.getDestPageIdx()
        if (uri != null && uri.isNotEmpty()) {
            handleUri(uri)
        } else if (page != null) {
            handlePage(page)
        }
    }

    private fun handleUri(uri: String) {
        val parsedUri = Uri.parse(uri)
        val intent = Intent(Intent.ACTION_VIEW, parsedUri)
        val context: Context = pdfView.context
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.w(TAG, "No activity found for URI: $uri")
        }
    }

    private fun handlePage(page: Int) {
        pdfView.jumpTo(page)
    }

}