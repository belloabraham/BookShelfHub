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
interface UserReviewDao {
    @Query("SELECT * FROM UserReview WHERE bookId = :bookId")
    fun getOptionalLiveUserReview(bookId:String): LiveData<Optional<UserReview>>

    @Query("SELECT * FROM UserReview WHERE verified = :isVerified")
    suspend fun getUserReviews(isVerified:Boolean): List<UserReview>

    @Query("UPDATE UserReview set verified =:isVerified where bookId =:bookId")
    suspend fun updateReview(bookId: String, isVerified:Boolean)

    @Query("DELETE FROM UserReview")
    suspend fun deleteAllReviews()

    @Query("SELECT * FROM UserReview WHERE bookId = :bookId")
    suspend fun getUserReview(bookId:String): Optional<UserReview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserReviews(userReviews: List<UserReview>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserReview(userReview: UserReview)
}