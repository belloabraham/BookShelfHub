package com.bookshelfhub.bookshelfhub.services.database.local.room

import android.content.Context
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.google.common.base.Optional

open class RoomDb(private val context:Context) {

    open fun getUser(): Optional<UserRecord> {
      return  RoomInstance.getDatabase(context).userDao().getUser()
    }

    open suspend fun addUser(userRecord:UserRecord){
        RoomInstance.getDatabase(context).userDao().addUser(userRecord)
    }
}