package com.bookshelfhub.bookshelfhub.services.authentication

import android.app.Activity
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.Phone


open class PhoneAuth(activity:Activity, phoneAuthViewModel: PhoneAuthViewModel
) :Phone(activity, phoneAuthViewModel) {
}