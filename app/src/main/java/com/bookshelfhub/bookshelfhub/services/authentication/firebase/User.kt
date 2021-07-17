package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import android.app.Activity
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


open class User {

    private var stringUtils:StringUtil? = null

    constructor(stringUtils: StringUtil){
        this.stringUtils = stringUtils
    }

    constructor()

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
            return stringUtils?.capitalize(it)
        }
        return null
    }

    open fun getPhone(): String? {
        return auth.currentUser?.phoneNumber
    }

    open fun signOut(signOutCompleted: () -> Unit) {
        val authStateListener =
            AuthStateListener { firebaseAuth ->
                signOutCompleted()
            }
        auth.addAuthStateListener(authStateListener)
        auth.signOut()
    }

}