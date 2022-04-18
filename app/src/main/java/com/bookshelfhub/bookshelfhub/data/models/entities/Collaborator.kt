package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Collaborator")
data class Collaborator(
    val pubId:String,
    @PrimaryKey
    val bookId:String
)