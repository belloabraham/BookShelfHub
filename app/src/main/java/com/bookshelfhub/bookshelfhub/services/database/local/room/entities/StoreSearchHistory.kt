package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchHistory

@Entity(tableName= "StoreSearchHistory")
data class StoreSearchHistory(
    override val isbn:String,
    override val title:String,
    override val userId:String,
    @PrimaryKey (autoGenerate = true)
    override val id:Long = 0
): ISearchHistory