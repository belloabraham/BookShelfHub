package com.bookshelfhub.bookshelfhub.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "Collaborators")
data class Collaborator(
    val collabId:String,
    @PrimaryKey
    val bookId:String,
    val commissionInPercentage: Double
)
