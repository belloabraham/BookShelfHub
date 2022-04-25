package com.bookshelfhub.bookshelfhub.data.repos.bookdownload

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState

interface IBookDownloadStateRepo {
    fun getLiveBookDownloadState(bookId: String): LiveData<BookDownloadState>

    suspend fun addDownloadState(bookDownloadState: BookDownloadState)

    suspend fun updatedDownloadState(bookId: String, hasError:Boolean)

    suspend fun updatedDownloadState(bookId: String, progress:Int)
}