package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.google.common.base.Optional

@Dao
abstract class UserReviewDao :BaseDao<UserReview> {

    @Query("SELECT * FROM UserReviews WHERE bookId = :bookId")
    abstract fun getOptionalLiveUserReview(bookId:String): LiveData<Optional<UserReview>>

    @Query("SELECT * FROM UserReviews WHERE verified = :isVerified")
    abstract suspend  fun getUserReviews(isVerified:Boolean): List<UserReview>

    @Query("UPDATE UserReviews set verified =:isVerified where bookId =:bookId")
    abstract suspend  fun updateReview(bookId: String, isVerified:Boolean)

    @Query("DELETE FROM UserReviews")
    abstract suspend  fun deleteAllReviews()

    @Query("SELECT * FROM UserReviews WHERE bookId = :bookId")
    abstract suspend  fun getUserReview(bookId:String): Optional<UserReview>

}