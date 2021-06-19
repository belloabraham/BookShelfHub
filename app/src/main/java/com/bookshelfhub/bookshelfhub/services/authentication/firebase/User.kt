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


open class User(private val stringUtils: StringUtil) {

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
            return stringUtils.capitalize(it)
        }
        return null
    }

    open fun getPhone(): String? {
        return auth.currentUser?.phoneNumber
    }

    open fun signOut(activity: Activity?, signOutCompleted: () -> Unit) {
        if (getAuthType()=="google.com"){
            activity?.let {
                googleSignOut(activity, signOutCompleted)
            }
        }else{
           phoneSignOut(signOutCompleted)
        }
    }

    private fun phoneSignOut(signOutCompleted: () -> Unit){
        val authStateListener =
            AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    signOutCompleted()
                }
            }
        auth.addAuthStateListener(authStateListener)
        auth.signOut()
    }

    private fun googleSignOut(activity: Activity, signOutCompleted: () -> Unit){
        val authStateListener =
            AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getString(R.string.gcp_web_client))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(activity, gso)
                    googleSignInClient.signOut().addOnCompleteListener {
                        if (it.isSuccessful){
                            signOutCompleted()
                        }
                    }
                }
            }
        auth.addAuthStateListener(authStateListener)
        auth.signOut()
    }
}