package com.bookshelfhub.core.data.repos.bookinterest

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.BookInterest
import java.util.*

interface IBookInterestRepo {
    suspend fun getBookInterest(userId: String): Optional<BookInterest>

    suspend fun updateRemoteUserBookInterest(bookInterest: BookInterest, userId: String): Void?
    fun getLiveBookInterest(userId: String): LiveData<Optional<BookInterest>>

    suspend fun addBookInterest(bookInterest: BookInterest)
}