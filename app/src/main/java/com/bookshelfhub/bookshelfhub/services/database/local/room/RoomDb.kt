package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User

open class RoomDb(private val context:Context) {

    open fun getUsers(): LiveData<List<User>> {
      return  RoomInstance.getDatabase(context).userDao().getUsers()
    }

    open suspend fun addUser(user:User){
        RoomInstance.getDatabase(context).userDao().addUser(user)
    }
}