package com.bookshelfhub.bookshelfhub.services.authentication

interface IPhoneAuth {
     fun startPhoneNumberVerification(phoneNumber: String)

     fun verifyPhoneNumberWithCode(code: String, wrongOTPErrorMsg: Int)

     fun resendVerificationCode(phoneNumber: String)
}