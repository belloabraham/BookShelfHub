package com.bookshelfhub.bookshelfhub.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "PubReferrers")
data class PubReferrers(
    val pubId:String,
    @PrimaryKey
    val isbn:String
)
