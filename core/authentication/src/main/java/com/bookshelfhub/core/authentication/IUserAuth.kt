package com.bookshelfhub.core.authentication

interface IUserAuth {
     suspend fun updateDisplayName(name:String)
     fun getPhotoUrl(): String?
     fun getIsUserAuthenticated(): Boolean
     fun getUserId(): String
     fun getEmail(): String?
     fun getAuthType(): String
     fun getName(): String?
     fun getPhone(): String?
     fun signOut()
}