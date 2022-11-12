package com.bookshelfhub.core.model.entities.remote

import com.bookshelfhub.core.model.entities.BookInterest
import com.bookshelfhub.core.model.entities.User


class RemoteUser(
    val user: User,
    val bookInterest: BookInterest?,
    val notificationToken: String?
)