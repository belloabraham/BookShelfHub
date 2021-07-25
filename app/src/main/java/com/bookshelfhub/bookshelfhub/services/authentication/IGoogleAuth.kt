package com.bookshelfhub.bookshelfhub.services.authentication

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface IGoogleAuth {
 fun authWithGoogle(idToken: String, authErrorMsg: String)
 fun signInOrSignUpWithGoogle(resultLauncher: ActivityResultLauncher<Intent>)
 fun signOut(signOutCompleted: () -> Unit)
}