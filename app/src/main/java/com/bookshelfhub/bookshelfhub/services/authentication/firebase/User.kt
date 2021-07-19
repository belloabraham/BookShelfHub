package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import com.bookshelfhub.bookshelfhub.extensions.capitalize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


open class User() {

    private val auth: FirebaseAuth = Firebase.auth

    open fun getIsUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    open fun getUserId(): String {
        return auth.currentUser!!.uid
    }

    open fun getEmail(): String? {
        return auth.currentUser?.email
    }

    open fun getAuthType(): String {
        var id=""
        for (i in auth.currentUser?.providerData!!){
            id = i.providerId
        }
        return id
    }

    open fun getName(): String? {
        auth.currentUser?.displayName?.let {
            return it.capitalize()
        }
        return null
    }

    open fun getPhone(): String? {
        return auth.currentUser?.phoneNumber
    }

    open fun signOut(signOutCompleted: () -> Unit) {
        val authStateListener =
            AuthStateListener { _ ->
                signOutCompleted()
            }
        auth.addAuthStateListener(authStateListener)
        auth.signOut()
    }

}