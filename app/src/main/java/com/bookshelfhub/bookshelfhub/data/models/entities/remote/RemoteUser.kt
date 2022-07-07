package com.bookshelfhub.bookshelfhub.data.models.entities.remote

import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User

class RemoteUser(
    val user: User,
    val bookInterest:BookInterest?,
    notificationToken: String?
){
    init {
        notificationToken?.let {
            if(it.isBlank())
               throw IllegalArgumentException("Invalid notification token")
        }
    }
}