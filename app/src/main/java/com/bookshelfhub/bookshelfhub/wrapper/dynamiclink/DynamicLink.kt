package com.bookshelfhub.bookshelfhub.wrapper.dynamiclink

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import javax.inject.Inject

class DynamicLink @Inject constructor  (domainPrefix:String, context: Context, appUtil: AppUtil) :FirebaseDLink(domainPrefix, context, appUtil) {
}