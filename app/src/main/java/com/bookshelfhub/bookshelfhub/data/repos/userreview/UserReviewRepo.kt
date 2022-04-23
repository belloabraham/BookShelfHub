package com.bookshelfhub.bookshelfhub.data.repos.userreview

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.sources.local.UserReviewDao
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.google.common.base.Optional
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserReviewRepo @Inject constructor(
    private val userReviewDao: UserReviewDao,
    private val remoteDataSource: IRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = IO
) :
    IUserReviewRepo {

     override suspend fun addUserReviews(userReviews: List<UserReview>) {
        withContext(ioDispatcher){userReviewDao.insertAllOrReplace(userReviews)}
    }

      override suspend fun getLiveUserReview(bookId:String, userId:String): LiveData<Optional<UserReview>> {
           if(!userReviewDao.getUserReview(bookId).isPresent){
             getRemoteUserReview(bookId, userId)
          }
         return userReviewDao.getOptionalLiveUserReview(bookId)
    }

    override suspend fun updateRemoteUserReview(
        userReview: UserReview,
        bookUpdatedValues: HashMap<String, FieldValue>?,
        bookId: String,
        userId: String): Void? {
        return withContext(ioDispatcher) {
            remoteDataSource.updateUserReview(
                bookUpdatedValues,
                userReview,
                RemoteDataFields.PUBLISHED_BOOKS_COLL,
                bookId,
                RemoteDataFields.REVIEWS_COLL,
                userId
            )
        }
    }

   override suspend fun updateRemoteUserReviews(
       userReviews: List<UserReview>,
       bookUpdatedValues: List<HashMap<String, FieldValue>>,
       userId: String): Void? {
      return withContext(ioDispatcher) {
          remoteDataSource.updateUserReviews(
              userReviews,
              RemoteDataFields.PUBLISHED_BOOKS_COLL,
              RemoteDataFields.REVIEWS_COLL,
              userId,
              bookUpdatedValues
          )
      }
   }

    private var remoteUserRetryInterval:Long = 1
    private val maxNoOfRetires = 5_000
    override suspend fun getRemoteUserReview(bookId:String, userId:String){
        try {
            val userReview = withContext(ioDispatcher) {
                remoteDataSource.getDataAsync(RemoteDataFields.PUBLISHED_BOOKS_COLL,
                    bookId, RemoteDataFields.REVIEWS_COLL,
                    userId, true, UserReview::class.java)
            }

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

    override suspend  fun getListOfBookReviews(bookId:String, limit:Long, excludedDocId:String): List<UserReview> {
       return withContext(ioDispatcher) {
           remoteDataSource.getListOfDataWhereAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
            RemoteDataFields.REVIEWS_COLL, UserReview::class.java,
            RemoteDataFields.VERIFIED, whereValue = true, limit, excludedDocId)
       }
    }

     override suspend fun updateReview(isbn: String, isVerified: Boolean) {
        return withContext(ioDispatcher){ userReviewDao.updateReview(isbn, isVerified)}
    }

     override suspend fun deleteAllReviews() {
        return  withContext(ioDispatcher){userReviewDao.deleteAllReviews()}
    }

     override suspend fun getUserReview(isbn: String): Optional<UserReview> {
        return withContext(ioDispatcher){userReviewDao.getUserReview(isbn)}
    }

     override suspend fun addUserReview(userReview: UserReview) {
         withContext(ioDispatcher){ userReviewDao.insertOrReplace(userReview)}
    }

    override suspend fun getUserReviews(isVerified: Boolean): List<UserReview> {
        return  withContext(ioDispatcher){ userReviewDao.getUserReviews(isVerified)}
    }


    
}