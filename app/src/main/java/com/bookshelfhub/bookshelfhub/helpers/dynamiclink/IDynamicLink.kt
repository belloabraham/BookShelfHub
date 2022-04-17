package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

import android.app.Activity
import android.content.Intent
import android.net.Uri

interface IDynamicLink {

    suspend fun generateShortLinkAsync(
        socialTitle: String,
        socialDescription: String,
        imageLink: String,
        uniqueId: String,
        minimumVCode: Int = 0
     ): Uri?

    fun getDeepLinkAsync(activity: Activity, onComplete: (Uri?) -> Unit)
}