package com.bookshelfhub.bookshelfhub.helpers.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

interface IPhoneAuth {
     fun sendVerificationCode(phoneNumber: String, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks)
     fun verifyPhoneNumberWithCode(code: String, storedVerificationId: String): Task<AuthResult>
     fun resendVerificationCode(phoneNumber: String, resendToken: PhoneAuthProvider.ForceResendingToken, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks)
     fun signInWithCredential(credential: PhoneAuthCredential)
}