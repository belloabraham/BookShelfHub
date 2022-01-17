package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import com.bookshelfhub.bookshelfhub.extensions.capitalize
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


open class UserAuth() : IUserAuth {

    private val auth: FirebaseAuth = Firebase.auth

    override fun getPhotoUrl(): String? {
        val photoUrl = auth.currentUser!!.photoUrl
      return photoUrl?.toString()?.replace("s96-c", "s492-c")
    }

    override fun getIsUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    override fun getUserId(): String {
        return auth.currentUser!!.uid
    }

    override fun getEmail(): String? {
        return auth.currentUser?.email
    }

    override fun getAuthType(): String {
        var id=""
        for (i in auth.currentUser?.providerData!!){
            id = i.providerId
        }
        return id
    }

    override fun getName(): String? {
       return auth.currentUser?.displayName?.capitalize()
    }

    override fun getPhone(): String? {
        return auth.currentUser?.phoneNumber
    }

    override fun signOut() {
        auth.signOut()
    }

}