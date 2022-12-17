package com.bookshelfhub.core.model.uistate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BookDownloadState")
class BookDownloadState(
     @PrimaryKey
     val bookId:String,
     val progress:Int,
     val hasError:Boolean = false
)