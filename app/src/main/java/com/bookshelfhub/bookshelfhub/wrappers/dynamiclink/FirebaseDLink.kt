package com.bookshelfhub.bookshelfhub.wrappers.dynamiclink

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import javax.inject.Inject

open class FirebaseDLink @Inject constructor(private val domainPrefix:String, private val context:Context, private val appUtil: AppUtil) :
    IDynamicLink {

    override fun getLinkAsync(socialTitle:String,
                              socialDescription:String,
                              imageLink:String,
                              uniqueId: String,
                              minimumVCode:Int, onComplete:(Uri?)->Unit){

        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = Uri.parse(String.format(context.getString(R.string.dlink_deeplink_domain), uniqueId))
            domainUriPrefix = domainPrefix
            androidParameters(context.packageName) {
                minimumVersion = minimumVCode
            }
            googleAnalyticsParameters {
                source = "user-$uniqueId"
                medium = "android-app"
                campaign = "user-referrer"
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

    override fun getDeepLinkAsync(activity:Activity, onComplete:(Uri?)->Unit){
        Firebase.dynamicLinks
            .getDynamicLink(activity.intent)
            .addOnSuccessListener(activity) {
                    pendingDynamicLinkData ->
                if (pendingDynamicLinkData!=null){
                    onComplete(pendingDynamicLinkData.link)
                }else{
                    onComplete(null)
                }
            }
            .addOnFailureListener(activity) { e ->
                onComplete(null)
            }
    }

}