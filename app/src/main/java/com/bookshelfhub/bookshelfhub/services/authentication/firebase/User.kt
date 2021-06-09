package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import android.net.Uri
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class User(private val stringUtils: StringUtil) {

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

        auth.currentUser?.let {
           it.email?.let {
               return AuthType.GOOGLE.ID
           }
            it.phoneNumber?.let {
                return AuthType.PHONE.ID
            }
        }
       return ""
    }

    open fun getName():String?{
        auth.currentUser?.displayName?.let {
            return  stringUtils.capitalize(it)
        }
        return  null
    }

    open fun getPhone():String?{
        return  auth.currentUser?.phoneNumber
    }

    open fun getProfilePicUri(): Uri?{
        return  auth.currentUser?.photoUrl
    }
}