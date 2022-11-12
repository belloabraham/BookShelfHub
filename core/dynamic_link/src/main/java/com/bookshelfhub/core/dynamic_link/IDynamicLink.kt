package com.bookshelfhub.core.dynamic_link

import android.app.Activity
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