package com.bookshelfhub.bookshelfhub.services.authentication

import android.app.Activity
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.Phone
import com.bookshelfhub.bookshelfhub.ui.welcome.WelcomeActivityViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


open class PhoneAuth(activity:Activity, welcomeActivityViewModel: WelcomeActivityViewModel
) :Phone(activity, welcomeActivityViewModel) {
}