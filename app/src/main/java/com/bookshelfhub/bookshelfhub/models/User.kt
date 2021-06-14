package com.bookshelfhub.bookshelfhub.models

import org.threeten.bp.LocalDateTime

class User: IUser {
    override var userId: String = ""
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
    override val deviceOs: String = ""
        get() = field
    override val lastUpdated: String=""
        get() = field
    override var uploaded: Boolean = false
        get() = field
}