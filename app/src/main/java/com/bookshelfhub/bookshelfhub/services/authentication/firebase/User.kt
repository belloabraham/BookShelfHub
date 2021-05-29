package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
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

    open fun getAuthType():String{
       return auth.currentUser!!.providerId
    }

    open fun getName():String?{
        return  auth.currentUser?.displayName
    }

    open fun getPhone():String?{
        return  auth.currentUser?.phoneNumber
    }

}