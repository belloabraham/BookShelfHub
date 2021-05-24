package com.bookshelfhub.bookshelfhub.services.authentication.firebase

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

open class Google (val activity: Activity, val googleAuthViewModel: GoogleAuthViewModel) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken( activity.getString(R.string.gcp_web_client))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    open fun authWithGoogle(idToken: String, errorMsg:String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                   googleAuthViewModel.setIsAuthenticatedCompleted(user!=null)
                } else {
                    googleAuthViewModel.setAuthenticationError(errorMsg)
                }
            }
    }

    open fun signInOrSignUpWithGoogle(resultLauncher: ActivityResultLauncher<Intent>){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }
}