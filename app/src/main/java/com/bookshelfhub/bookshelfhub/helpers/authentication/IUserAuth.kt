package com.bookshelfhub.bookshelfhub.helpers.authentication

interface IUserAuth {
     fun getPhotoUrl(): String?
     fun getIsUserAuthenticated(): Boolean
     fun getUserId(): String
     fun getEmail(): String?
     fun getAuthType(): String
     fun getName(): String?
     fun getPhone(): String?
     fun signOut()
}