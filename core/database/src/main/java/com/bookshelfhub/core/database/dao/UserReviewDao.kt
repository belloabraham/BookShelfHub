package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.UserReview
import java.util.*

@Dao
abstract class UserReviewDao : BaseDao<UserReview> {

    @Query("SELECT * FROM UserReviews WHERE bookId = :bookId")
    abstract fun getOptionalLiveUserReview(bookId:String): LiveData<Optional<UserReview>>

    @Query("SELECT * FROM UserReviews WHERE isVerified = :isVerified")
    abstract suspend  fun getUserReviews(isVerified:Boolean): List<UserReview>

    @Query("UPDATE UserReviews set isVerified =:isVerified where bookId =:bookId")
    abstract suspend  fun updateReview(bookId: String, isVerified:Boolean)

    @Query("DELETE FROM UserReviews")
    abstract suspend  fun deleteAllReviews()

    @Query("SELECT * FROM UserReviews WHERE bookId = :bookId")
    abstract suspend  fun getUserReview(bookId:String): Optional<UserReview>

}