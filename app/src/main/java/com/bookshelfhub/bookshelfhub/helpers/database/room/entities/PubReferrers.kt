package com.bookshelfhub.bookshelfhub.helpers.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PubReferrers")
data class PubReferrers(
    val pubId:String,
    @PrimaryKey
    val isbn:String
)
