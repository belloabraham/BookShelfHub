package com.bookshelfhub.bookshelfhub.services.authentication

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface IGoogleAuth {
 fun authWithGoogle(idToken: String): Task<AuthResult>
 fun signInOrSignUpWithGoogle(resultLauncher: ActivityResultLauncher<Intent>)
 fun signOut(): Task<Void>
}