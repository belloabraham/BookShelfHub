package com.bookshelfhub.core.data

sealed class SealedState<T>{
    class Success<T>(val data:T?) : SealedState<T>()
    open class Error<T>(type:Exception) : SealedState<T>()
    class Loading<T>(val data:T?):SealedState<T>()
}