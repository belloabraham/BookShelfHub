package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bookshelfhub.bookshelfhub.data.Config
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.UserReviewDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.google.common.base.Optional
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.remoteconfig.internal.Code
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserReviewRepo @Inject constructor(private val userReviewDao: UserReviewDao, private val remoteDataSource: IRemoteDataSource) {

     suspend fun addUserReviews(userReviews: List<UserReview>) {
        withContext(IO){userReviewDao.addUserReviews(userReviews)}
    }

      suspend fun getLiveUserReview(bookId:String, userId:String): LiveData<Optional<UserReview>> {
           if(!userReviewDao.getUserReview(bookId).isPresent){
             getRemoteUserReview(bookId, userId)
          }
         return userReviewDao.getOptionalLiveUserReview(bookId)
    }

    var remoteUserRetryInterval:Long = 1
    private suspend fun getRemoteUserReview(bookId:String, userId:String){
        try {
            val userReview = remoteDataSource.getDataAsync(RemoteDataFields.PUBLISHED_BOOKS_COLL,bookId, RemoteDataFields.REVIEWS_COLL, userId, true, UserReview::class.java)
            userReview?.let {
                userReviewDao.addUserReview(it)
            }
            remoteUserRetryInterval = 1
        }catch (e:Exception){
            if(remoteUserRetryInterval < Config.MAX_NETWORK_RETRIES){
                delay(200*remoteUserRetryInterval)
                remoteUserRetryInterval++
                getRemoteUserReview(bookId, userId)
            }
        }
    }

    fun getTop3UserReviews(){
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