package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class User {

    open fun getIsUserAuthenticated():Boolean{
        val auth: FirebaseAuth = Firebase.auth
        return auth.currentUser!=null
    }

}