package com.bookshelfhub.bookshelfhub.data.repos.user

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.models.entities.remote.RemoteUser
import com.google.common.base.Optional

interface IUserRepo {
    suspend fun getRemoteUserDataSnapshot(userId: String): RemoteUser?

    suspend fun uploadNotificationToken(notificationToken: String, userId: String): Void?

    suspend fun uploadUser(user: User, userId: String): Void?

    suspend fun uploadRemoteUser(remoteUser: RemoteUser, userId: String): Void?

    suspend fun getUser(userId: String): Optional<User>
    fun getLiveUser(userId: String): LiveData<User>

    suspend fun addUser(user: User)
    suspend fun deleteUserRecord()
}