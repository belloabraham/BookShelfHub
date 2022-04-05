package com.bookshelfhub.bookshelfhub.helpers.authentication.firebase

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.bookshelfhub.bookshelfhub.helpers.authentication.IGoogleAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class GoogleAuth(private val activity: Activity, private val gcpWebClient:Int) :
    IGoogleAuth {


    private val firebaseAuth = Firebase.auth
    private var googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(gcpWebClient))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)

    }

    override fun authWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
      return  firebaseAuth.signInWithCredential(credential)
    }

    override fun signInOrSignUpWithGoogle(resultLauncher: ActivityResultLauncher<Intent>){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    override fun signOut(): Task<Void> {
       return googleSignInClient.signOut()
    }
}