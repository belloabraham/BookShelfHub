package com.bookshelfhub.bookshelfhub.wrapper.dynamiclink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2

open class FirebaseDLink (private val domainPrefix:String, private val context:Context, private val userAuth: UserAuth, private val appUtil: AppUtil) {

    open fun getLinkAsync(socialTitle:String,
                     socialDescription:String,
                     imageLink:String,
                     uniqueId:String = userAuth.getUserId(),
                     minimumVCode:Int=appUtil.getAppVersionCode().toInt(), onComplete:(Uri?)->Unit){

        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = Uri.parse(String.format(context.getString(R.string.bsh_website), uniqueId))
            domainUriPrefix = domainPrefix
            androidParameters(context.packageName) {
                minimumVersion = minimumVCode
            }
            googleAnalyticsParameters {
                source = "user-${userAuth.getUserId()}"
                medium = "android-app"
                campaign = "android-app-referrer"
            }
            socialMetaTagParameters {
                title = socialTitle
                description = socialDescription
                imageUrl = Uri.parse(imageLink)
            }
        }.addOnSuccessListener { (shortLink, _) ->
            onComplete(shortLink)
        }.addOnFailureListener {
            onComplete(null)
        }

    }

    open fun getDeepLinkAsync(activity:Activity, onComplete:(Uri?)->Unit){
        Firebase.dynamicLinks
            .getDynamicLink(activity.intent)
            .addOnSuccessListener(activity) {
                    pendingDynamicLinkData ->
                onComplete(pendingDynamicLinkData.link)
            }
            .addOnFailureListener(activity) { e ->
                onComplete(null)
            }
    }

}