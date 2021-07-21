package com.bookshelfhub.pdfviewer.extensions

class PageRenderingException(val page: Int, cause: Throwable?) : Exception(cause)