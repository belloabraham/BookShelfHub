package com.bookshelfhub.bookshelfhub.data.repos.user

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.models.entities.remote.RemoteUser
import com.bookshelfhub.bookshelfhub.data.sources.local.UserDao
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val userDao: UserDao,
    private val remoteDataSource: IRemoteDataSource,
    private val worker: Worker) :
    IUserRepo {


    override suspend fun getRemoteUserDataSnapshot(userId:String): RemoteUser? {
       return  remoteDataSource.getDataAsync(RemoteDataFields.USERS_COLL, userId, RemoteUser::class.java)
    }

    override suspend fun uploadNotificationToken(notificationToken:String, userId: String): Void? {
      return  remoteDataSource.addDataAsync(notificationToken,
            RemoteDataFields.USERS_COLL, userId, RemoteDataFields.NOTIFICATION_TOKEN)
    }

    override suspend fun uploadUser(user:User, userId: String): Void? {
      return  remoteDataSource.addDataAsync(RemoteDataFields.USERS_COLL, userId, RemoteDataFields.USER, user)
    }

    override suspend fun uploadRemoteUser(remoteUser: RemoteUser, userId: String): Void? {
        return remoteDataSource.addDataAsync(RemoteDataFields.USERS_COLL, userId, remoteUser)
    }

     override suspend fun getUser(userId:String): Optional<User> {
        return withContext(IO){
            userDao.getUser(userId)
        }
    }

    override fun getLiveUser(userId:String): LiveData<User> {
        return  userDao.getLiveUser(userId)
    }


    override suspend fun addUser(user: User){
        withContext(IO) {
            userDao.insertOrReplace(user)
        }

        if(!user.uploaded){
            val oneTimeUserDataUpload =
                OneTimeWorkRequestBuilder<UploadUserData>()
                    .setConstraints(Constraint.getConnected())
                    .build()
            worker.enqueueUniqueWork(Tag.addUserUniqueWorkDatUpload, ExistingWorkPolicy.REPLACE, oneTimeUserDataUpload)
        }
    }

     override suspend fun deleteUserRecord() {
        return withContext(IO) { userDao.deleteUserRecord()}
    }

}