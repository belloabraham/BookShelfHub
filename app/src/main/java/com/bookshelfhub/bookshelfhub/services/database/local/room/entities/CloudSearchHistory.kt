package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchHistory

@Entity(tableName= "CloudSearchHistory")
data class CloudSearchHistory(
    @PrimaryKey
    override val isbn:String,
    override val title:String,
    override val userId:String,
): ISearchHistory