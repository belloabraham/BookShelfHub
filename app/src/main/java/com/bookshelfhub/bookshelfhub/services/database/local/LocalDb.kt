package com.bookshelfhub.bookshelfhub.services.database.local

import android.content.Context
import com.bookshelfhub.bookshelfhub.services.database.local.room.RoomDb
import javax.inject.Inject

class LocalDb @Inject constructor(context: Context): RoomDb(context) {

}