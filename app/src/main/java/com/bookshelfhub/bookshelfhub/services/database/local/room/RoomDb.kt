package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {

    open fun getUser(): Optional<User> {
      return  RoomInstance.getDatabase(context).userDao().getUser()
    }

    open suspend fun addUser(user:User){
        RoomInstance.getDatabase(context).userDao().addUser(user)
    }
}