package com.bookshelfhub.core.data.repos.bookdownload

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.uistate.BookDownloadState
import java.util.*

interface IBookDownloadStateRepo {
    fun getLiveBookDownloadState(bookId: String): LiveData<Optional<BookDownloadState>>

    suspend fun addDownloadState(bookDownloadState: BookDownloadState)

    suspend fun updatedDownloadState(bookId: String, hasError:Boolean)

    suspend fun updatedDownloadState(bookId: String, progress:Int)

    suspend fun deleteDownloadState(bookDownloadState: BookDownloadState)
}