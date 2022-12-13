package com.bookshelfhub.core.data.repos.user_review

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.UserReview
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class UserReviewRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
) : IUserReviewRepo {
    private val userReviewDao = appDatabase.getUserReviewsDao()
    private val ioDispatcher: CoroutineDispatcher = IO

     override suspend fun addUserReviews(userReviews: List<UserReview>) {
        withContext(ioDispatcher){userReviewDao.insertAllOrReplace(userReviews)}
    }

      override fun getLiveUserReview(bookId:String): LiveData<Optional<UserReview>> {
         return userReviewDao.getOptionalLiveUserReview(bookId)
    }

    override suspend fun updateRemoteUserReview(
        userReview: UserReview,
        bookUpdatedValues: HashMap<String, FieldValue>?,
        bookId: String,
        userId: String): Void? {
        return remoteDataSource.updateUserReview(
                bookUpdatedValues,
                userReview,
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                bookId,
                RemoteDataFields.REVIEWS_COLL,
                userId
            )
    }

   override suspend fun updateRemoteUserReviews(
       userReviews: List<UserReview>,
       bookUpdatedValues: List<HashMap<String, FieldValue>>,
       userId: String): Void? {
          return remoteDataSource.updateUserReviews(
              userReviews,
              RemoteDataFields.PUBLISHED_BOOKS_COLL,
              RemoteDataFields.REVIEWS_COLL,
              userId,
              bookUpdatedValues
          )
   }


    override suspend fun getRemoteUserReview(bookId:String, userId:String): UserReview? {
       return  remoteDataSource.getDataAsync(RemoteDataFields.PUBLISHED_BOOKS_COLL,
            bookId, RemoteDataFields.REVIEWS_COLL,
            userId, UserReview::class.java)
    }

    override suspend  fun getRemoteListOfBookReviews(bookId:String, limit:Long, excludedDocId:String): List<UserReview> {
       return remoteDataSource.getListOfDataWhereAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
            RemoteDataFields.REVIEWS_COLL, UserReview::class.java,
            RemoteDataFields.VERIFIED, whereValue = true, limit, excludedDocId)
    }

     override suspend fun updateReview(isbn: String, isVerified: Boolean) {
        return withContext(ioDispatcher){ userReviewDao.updateReview(isbn, isVerified)}
    }

     override suspend fun deleteAllReviews() {
        return  withContext(ioDispatcher){userReviewDao.deleteAllReviews()}
    }

     override suspend fun getUserReview(bookId: String): Optional<UserReview> {
        return withContext(ioDispatcher){userReviewDao.getUserReview(bookId)}
    }

     override suspend fun addUserReview(userReview: UserReview) {
        return withContext(ioDispatcher){ userReviewDao.insertOrReplace(userReview)}
    }

    override suspend fun getUserReviews(isVerified: Boolean): List<UserReview> {
        return  withContext(ioDispatcher){ userReviewDao.getUserReviews(isVerified)}
    }


    
}