package com.bookshelfhub.bookshelfhub.data.models.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BookDownloadStates")
class BookDownloadState(
     @PrimaryKey
     val bookId:String,
     val progress:Int,
     val hasError:Boolean = false
)