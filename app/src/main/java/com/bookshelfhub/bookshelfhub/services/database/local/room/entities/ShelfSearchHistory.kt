package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.ISearchHistory

@Entity(tableName= "ShelfSearchHistory")
data class ShelfSearchHistory(
    @PrimaryKey (autoGenerate = true)
    override val id:Long,
    override val isbn:String,
    override val title:String,
    override val userId:String,
): ISearchHistory