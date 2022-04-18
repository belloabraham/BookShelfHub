package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.UserDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val userDao: UserDao,
    private val remoteDataSource: IRemoteDataSource,
    private val worker: Worker) {

    private var userDocSnapShot: MutableLiveData<DocumentSnapshot?> = MutableLiveData()

    private var liveUserDataSubscription: ListenerRegistration? =null

    fun getLiveRemoteUserDataSnapshot(userId:String):LiveData<DocumentSnapshot?>{
        liveUserDataSubscription = remoteDataSource.getLiveDataAsync(RemoteDataFields.USERS_COLL, userId, retry = true){ docSnapShot, _ ->
            userDocSnapShot.postValue(docSnapShot)
        }
        return userDocSnapShot
    }

    fun unsubscribeFromLiveUserData(){
        liveUserDataSubscription?.remove()
    }

     suspend fun getUser(userId:String): Optional<User> {
        return withContext(IO){
            userDao.getUser(userId)
        }
    }

    fun getLiveUser(userId:String): LiveData<User> {
        return  userDao.getLiveUser(userId)
    }

    suspend fun addUser(user: User){
        withContext(IO) {
            userDao.addUser(user)
        }
        val oneTimeUserDataUpload =
            OneTimeWorkRequestBuilder<UploadUserData>()
                .setConstraints(Constraint.getConnected())
                .build()
        worker.enqueueUniqueWork(Tag.addUserUniqueWorkDatUpload, ExistingWorkPolicy.REPLACE, oneTimeUserDataUpload)
    }

     suspend fun deleteUserRecord() {
        return withContext(IO) { userDao.deleteUserRecord()}
    }

}