package com.bookshelfhub.downloadmanager


class Error {
    private var isServerError = false
    private var isConnectionError = false
    private var serverErrorMessage: String? = null
    private var headerFields: Map<String, List<String>>? = null
    private var connectionException: Throwable? = null
    private var responseCode = 0

    fun isServerError(): Boolean {
        return isServerError
    }

    fun setServerError(serverError: Boolean) {
        isServerError = serverError
    }

    fun isConnectionError(): Boolean {
        return isConnectionError
    }

    fun setConnectionError(connectionError: Boolean) {
        isConnectionError = connectionError
    }

    fun setServerErrorMessage(serverErrorMessage: String?) {
        this.serverErrorMessage = serverErrorMessage
    }

    fun getServerErrorMessage(): String? {
        return serverErrorMessage
    }

    fun setHeaderFields(headerFields: Map<String, List<String>>?) {
        this.headerFields = headerFields
    }

    fun getHeaderFields(): Map<String, List<String>>? {
        return headerFields
    }

    fun setConnectionException(connectionException: Throwable?) {
        this.connectionException = connectionException
    }

    fun getConnectionException(): Throwable? {
        return connectionException
    }

    fun setResponseCode(responseCode: Int) {
        this.responseCode = responseCode
    }

    fun getResponseCode(): Int {
        return responseCode
    }
}