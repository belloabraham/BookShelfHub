package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.UserDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val userDao: UserDao,
    private val worker: Worker) {

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
        val connected = Constraint.getConnected()
        val oneTimeUserDataUpload =
            OneTimeWorkRequestBuilder<UploadUserData>()
                .setConstraints(connected)
                .build()
        worker.enqueueUniqueWork("", ExistingWorkPolicy.REPLACE, oneTimeUserDataUpload)
    }

     suspend fun deleteUserRecord() {
        return withContext(IO) { userDao.deleteUserRecord()}
    }

}