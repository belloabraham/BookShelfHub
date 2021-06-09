package com.bookshelfhub.bookshelfhub.models

import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IUser

class User: IUser {
    override val uId: String = ""
        get() = field
    override var name: String = ""
        get() = field
    override var email: String = ""
        get() = field
    override var phone: String = ""
        get() = field
    override var photoUri: String? = null
        get() = field
    override val authType: String = ""
        get() = field
    override val appVersion: String = ""
        get() = field
    override val device: String = ""
        get() = field
    override var uploaded: Boolean = false
        get() = field
}