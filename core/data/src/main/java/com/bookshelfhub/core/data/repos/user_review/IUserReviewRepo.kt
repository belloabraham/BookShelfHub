package com.bookshelfhub.core.data.repos.user_review

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.UserReview
import com.google.firebase.firestore.FieldValue
import java.util.*

interface IUserReviewRepo {
    suspend fun addUserReviews(userReviews: List<UserReview>)

    fun getLiveUserReview(bookId: String): LiveData<Optional<UserReview>>

    suspend fun updateRemoteUserReview(
        userReview: UserReview,
        bookUpdatedValues: Map<String, FieldValue>?,
        bookId: String,
        userId: String
    ): Void?

    suspend fun updateRemoteUserReviews(
        userReviews: List<UserReview>,
        bookUpdatedValues: List<Map<String, FieldValue>>,
        userId: String
    ): Void?

    suspend fun getRemoteUserReview(bookId: String, userId: String):UserReview?
    suspend fun getRemoteListOfBookReviews(
        bookId: String,
        limit: Long,
        excludedDocId: String
    ): List<UserReview>

    suspend fun updateReview(isbn: String, isVerified: Boolean)

    suspend fun deleteAllReviews()

    suspend fun getUserReview(bookId: String): Optional<UserReview>

    suspend fun addUserReview(userReview: UserReview)

    suspend fun getUserReviews(isVerified: Boolean): List<UserReview>
}