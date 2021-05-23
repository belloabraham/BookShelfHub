package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class User {

    private val auth: FirebaseAuth = Firebase.auth

    open fun getIsUserAuthenticated():Boolean{
        return auth.currentUser!=null
    }

    open fun getUserId():String{
      return  auth.currentUser!!.uid
    }

    open fun getEmail():String?{
        return  auth.currentUser?.email
    }

}