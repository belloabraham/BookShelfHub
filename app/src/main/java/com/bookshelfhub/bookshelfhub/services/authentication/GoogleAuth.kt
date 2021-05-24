package com.bookshelfhub.bookshelfhub.services.authentication

import android.app.Activity
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.Google

open class GoogleAuth(activity: Activity, googleAuthViewModel:GoogleAuthViewModel):Google(activity, googleAuthViewModel) {
}