package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import com.bookshelfhub.bookshelfhub.extensions.capitalize
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


open class FBUserAuth() : IUserAuth {

    private val auth: FirebaseAuth = Firebase.auth

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
        auth.currentUser?.displayName?.let {
            return it.capitalize()
        }
        return null
    }

    override fun getPhone(): String? {
        return auth.currentUser?.phoneNumber
    }

    override fun signOut(signOutCompleted: () -> Unit) {
        val authStateListener =
            AuthStateListener { _ ->
                signOutCompleted()
            }
        auth.addAuthStateListener(authStateListener)
        auth.signOut()
    }

}