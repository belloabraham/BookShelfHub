package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

import android.app.Activity
import android.net.Uri

interface IDynamicLink {

     fun generateShortLinkAsync(
        socialTitle: String,
        socialDescription: String,
        imageLink: String,
        uniqueId: String,
        minimumVCode: Int = 0, onComplete: (Uri?) -> Unit
    )
     fun getDeepLinkAsync(activity: Activity, onComplete: (Uri?) -> Unit)
}