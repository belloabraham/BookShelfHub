package com.bookshelfhub.downloadmanager

data class Response(
    var error: Error? = null,
    var isSuccessful: Boolean = false,
    var isPaused: Boolean = false,
    var isCancelled: Boolean = false
)
