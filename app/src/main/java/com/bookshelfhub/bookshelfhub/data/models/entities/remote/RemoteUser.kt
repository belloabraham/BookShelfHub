package com.bookshelfhub.bookshelfhub.data.models.entities.remote

import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User

class RemoteUser(
    val user: User,
    val bookInterest:BookInterest?,
    val notificationToken: String?
)