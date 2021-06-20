package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class Google(private val activity: Activity, private val googleAuthViewModel: GoogleAuthViewModel?) {


    private val firebaseAuth = Firebase.auth
    private var googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken( activity.getString(R.string.gcp_web_client))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)

    }

    open fun authWithGoogle(idToken: String, authErrorMsg:String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    googleAuthViewModel?.setIsNewUser(task.result!!.additionalUserInfo!!.isNewUser)
                    googleAuthViewModel?.setIsAuthenticatedSuccessful(true)
                } else {
                    googleAuthViewModel?.setAuthenticationError(authErrorMsg)
                }
                googleAuthViewModel?.setIsAuthenticationComplete(true)
            }
    }

    open fun signInOrSignUpWithGoogle(resultLauncher: ActivityResultLauncher<Intent>){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    open fun signOut(signOutCompleted: () -> Unit){
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful){
                signOutCompleted()
            }
        }
    }
}