package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

import android.app.Activity
import android.content.Intent
import android.net.Uri

interface IDynamicLink {

    suspend fun generateShortDynamicLinkAsync(
        socialTitle: String,
        socialDescription: String,
        imageUri: String,
        uniqueId: String,
        minimumVCode: Int = 0
     ): Uri?

    fun getDeepLinkFromDynamicLinkAsync(activity: Activity, onComplete: (Uri?) -> Unit)
}