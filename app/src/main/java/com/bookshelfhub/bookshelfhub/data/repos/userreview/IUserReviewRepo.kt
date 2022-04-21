package com.bookshelfhub.bookshelfhub.data.repos.userreview

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.google.common.base.Optional
import com.google.firebase.firestore.FieldValue

interface IUserReviewRepo {
    suspend fun addUserReviews(userReviews: List<UserReview>)

    suspend fun getLiveUserReview(bookId: String, userId: String): LiveData<Optional<UserReview>>

    suspend fun updateRemoteUserReview(
        userReview: UserReview,
        bookUpdatedValues: HashMap<String, FieldValue>?,
        bookId: String,
        userId: String
    ): Void?

    suspend fun updateRemoteUserReviews(
        userReviews: List<UserReview>,
        bookUpdatedValues: List<HashMap<String, FieldValue>>,
        userId: String
    ): Void?

    suspend fun getRemoteUserReview(bookId: String, userId: String)
    suspend fun getListOfBookReviews(
        bookId: String,
        limit: Long,
        excludedDocId: String
    ): List<UserReview>

    suspend fun updateReview(isbn: String, isVerified: Boolean)

    suspend fun deleteAllReviews()

    suspend fun getUserReview(isbn: String): Optional<UserReview>

    suspend fun addUserReview(userReview: UserReview)

    suspend fun getUserReviews(isVerified: Boolean): List<UserReview>
}