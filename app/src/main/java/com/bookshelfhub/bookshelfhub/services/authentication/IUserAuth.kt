package com.bookshelfhub.bookshelfhub.services.authentication

interface IUserAuth {
    open fun getIsUserAuthenticated(): Boolean

    open fun getUserId(): String

    open fun getEmail(): String?

    open fun getAuthType(): String

    open fun getName(): String?

    open fun getPhone(): String?

    open fun signOut(signOutCompleted: () -> Unit)
}