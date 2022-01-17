package com.bookshelfhub.bookshelfhub.services.authentication

import com.google.android.gms.tasks.Task

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