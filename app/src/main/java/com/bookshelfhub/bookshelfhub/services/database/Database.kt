package com.bookshelfhub.bookshelfhub.services.database

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import javax.inject.Inject

class Database @Inject constructor(private val localDb: LocalDb, private val cloudDb:CloudDb) {

    fun getUsers(): LiveData<List<User>> {
        return localDb.getUsers()
    }

    suspend fun addUser(user:User){
        localDb.addUser(user)
        //Todo a one time request worker that add user to the cloud that requires connection
        //Todo A periodic request worker that Add user to the cloud that requires connection
        //Todo using cloud db making sure the data is merged
    }
}