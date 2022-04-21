package com.bookshelfhub.bookshelfhub.data.repos.bookinterest

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.google.common.base.Optional

interface IBookInterestRepo {
    suspend fun getBookInterest(userId: String): Optional<BookInterest>

    suspend fun updateRemoteUserBookInterest(bookInterest: BookInterest, userId: String): Void?
    fun getLiveBookInterest(userId: String): LiveData<Optional<BookInterest>>

    suspend fun addBookInterest(bookInterest: BookInterest)
}