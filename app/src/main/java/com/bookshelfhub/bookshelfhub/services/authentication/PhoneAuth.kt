package com.bookshelfhub.bookshelfhub.services.authentication

import android.app.Activity
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.Phone
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


open class PhoneAuth( activity:Activity) :Phone(activity) {
}