package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.UserReviewDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserReviewRepo @Inject constructor(private val userReviewDao: UserReviewDao, private val remoteDataS: IRemoteDataSource) {

     fun getLiveUserReview(isbn: String): LiveData<Optional<UserReview>> {
        return  userReviewDao.getLiveUserReview(isbn)
    }

     suspend fun addUserReviews(userReviews: List<UserReview>) {
        withContext(IO){userReviewDao.addUserReviews(userReviews)}
    }


     suspend fun updateReview(isbn: String, isVerified: Boolean) {
        return withContext(IO){ userReviewDao.updateReview(isbn, isVerified)}
    }

     suspend fun deleteAllReviews() {
        return  withContext(IO){userReviewDao.deleteAllReviews()}
    }

     suspend fun getUserReview(isbn: String): Optional<UserReview> {
        return withContext(IO){userReviewDao.getUserReview(isbn)}
    }

     suspend fun addUserReview(userReview: UserReview) {
         withContext(IO){ userReviewDao.addUserReview(userReview)}
    }

    suspend fun getUserReviews(isVerified: Boolean): List<UserReview> {
        return  withContext(IO){ userReviewDao.getUserReviews(isVerified)}
    }
    
}