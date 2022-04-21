package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.UserReviewDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.google.common.base.Optional
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserReviewRepo @Inject constructor(
    private val userReviewDao: UserReviewDao,
    private val remoteDataSource: IRemoteDataSource) {

     suspend fun addUserReviews(userReviews: List<UserReview>) {
        withContext(IO){userReviewDao.insertAllOrReplace(userReviews)}
    }

      suspend fun getLiveUserReview(bookId:String, userId:String): LiveData<Optional<UserReview>> {
           if(!userReviewDao.getUserReview(bookId).isPresent){
             getRemoteUserReview(bookId, userId)
          }
         return userReviewDao.getOptionalLiveUserReview(bookId)
    }

    suspend fun updateRemoteUserReview(
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
            userId)
    }

   suspend fun updateRemoteUserReviews(
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

    private var remoteUserRetryInterval:Long = 1
    private val maxNoOfRetires = 5_000
    private suspend fun getRemoteUserReview(bookId:String, userId:String){
        try {
            val userReview = remoteDataSource.getDataAsync(RemoteDataFields.PUBLISHED_BOOKS_COLL,bookId, RemoteDataFields.REVIEWS_COLL, userId, true, UserReview::class.java)
            userReview?.let {
                userReviewDao.insertOrReplace(it)
            }
            remoteUserRetryInterval = 1
        }catch (e:Exception){
            Timber.e(e)
            if(remoteUserRetryInterval < maxNoOfRetires){
                delay(200*remoteUserRetryInterval)
                remoteUserRetryInterval++
                getRemoteUserReview(bookId, userId)
            }
        }
    }

    suspend  fun getListOfBookReviews(bookId:String, limit:Long, excludedDocId:String): List<UserReview> {
       return remoteDataSource.getListOfDataWhereAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
            RemoteDataFields.REVIEWS_COLL, UserReview::class.java,
            RemoteDataFields.VERIFIED, whereValue = true, limit, excludedDocId)
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
         withContext(IO){ userReviewDao.insertOrReplace(userReview)}
    }

    suspend fun getUserReviews(isVerified: Boolean): List<UserReview> {
        return  withContext(IO){ userReviewDao.getUserReviews(isVerified)}
    }


    
}