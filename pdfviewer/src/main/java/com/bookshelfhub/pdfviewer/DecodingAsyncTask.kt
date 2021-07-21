package com.bookshelfhub.pdfviewer

import android.os.AsyncTask
import com.bookshelfhub.pdfviewer.source.DocumentSource

import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import com.shockwave.pdfium.util.Size

import java.lang.ref.WeakReference


class DecodingAsyncTask( private val docSource: DocumentSource?,
                         private val password: String?,
                         private val userPages: IntArray?,
                          pdfView: PDFView?,
                         private val pdfiumCore: PdfiumCore) : AsyncTask<Void, Void, Throwable>() {


    private var cancelled = false

    private var pdfViewReference: WeakReference<PDFView> = WeakReference(pdfView)
    private var pdfFile: PdfFile? = null

    override fun doInBackground(vararg params: Void?): Throwable? {
        return try {
            val pdfView: PDFView? = pdfViewReference.get()
            if (pdfView != null) {
                val pdfDocument: PdfDocument =
                    docSource!!.createDocument(pdfView.context, pdfiumCore, password)
                pdfFile = PdfFile(
                    pdfiumCore,
                    pdfDocument,
                    pdfView.getPageFitPolicy(),
                    getViewSize(pdfView),
                    userPages,
                    pdfView.isSwipeVertical(),
                    pdfView.getSpacingPx(),
                    pdfView.isAutoSpacingEnabled(),
                    pdfView.isFitEachPage()
                )
                null
            } else {
                NullPointerException("pdfView == null")
            }
        } catch (t: Throwable) {
            t
        }
    }

    private fun getViewSize(pdfView: PDFView): Size {
        return Size(pdfView.width, pdfView.height)
    }

    override fun onPostExecute(t: Throwable?) {
        val pdfView: PDFView? = pdfViewReference?.get()
        if (pdfView != null) {
            if (t != null) {
                pdfView.loadError(t)
                return
            }
            if (!cancelled) {
                pdfView.loadComplete(pdfFile!!)
            }
        }
    }

    override fun onCancelled() {
        cancelled = true
    }

}