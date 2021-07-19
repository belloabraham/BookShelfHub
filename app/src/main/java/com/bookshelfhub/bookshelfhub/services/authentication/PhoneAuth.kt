package com.bookshelfhub.bookshelfhub.services.authentication

import android.app.Activity
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.Phone


open class PhoneAuth(activity:Activity, phoneAuthViewModel: PhoneAuthViewModel, wrongOTPErrorMsg:Int, tooManyReqErrorMsg:Int, otherAuthErrorMsg:Int
) :Phone(activity, phoneAuthViewModel, wrongOTPErrorMsg, tooManyReqErrorMsg, otherAuthErrorMsg) {
}