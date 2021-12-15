package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

import android.app.Activity
import android.content.Context
import android.content.Intent
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

class Firebase @Inject constructor(private val domainPrefix:String, private val context:Context) :
    IDynamicLink {

    override fun generateShortLinkAsync(socialTitle:String,
                                        socialDescription:String,
                                        imageLink:String,
                                        uniqueId: String,
                                        minimumVCode:Int, onComplete:(Uri?)->Unit){

        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {

            /**
             * Deep link: The link to be received by whoever opened the generated link  with the app
             */
            link = Uri.parse(String.format(context.getString(R.string.dlink_deeplink_domain), uniqueId))

            /**
             * Domain prefix for the generated link
             */
            domainUriPrefix = domainPrefix
            androidParameters(context.packageName) {
                /**
                 * Minimum app version that can open the link as lover app version will be taken to Playstore
                 * for update in other to open the link
                 */
                minimumVersion = minimumVCode
            }

            /**
             * Unique Id here should be something like the user Id
             */
            googleAnalyticsParameters {
                source = "user-$uniqueId"
                medium = "android-app"
                campaign = "user-referrer"
            }

            /**
             * Information that will show when link is shares on social media platform
             */
            socialMetaTagParameters {
                title = socialTitle
                description = socialDescription
                imageUrl = Uri.parse(imageLink)
            }
        }.addOnSuccessListener { (generatedLink, _) ->
            onComplete(generatedLink)
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    /**
     * Used to get the link deep link from the generated Link
     */
    override fun getDeepLinkAsync(activity:Activity, onComplete:(Uri?)->Unit){
        Firebase.dynamicLinks
            .getDynamicLink(activity.intent)
            .addOnCompleteListener {
                onComplete(null)
            }
           /* .addOnSuccessListener(activity) {
                    pendingDynamicLinkData ->
                onComplete(null)
                if(pendingDynamicLinkData==null){
                  //  onComplete(null)
                }else{
                    //onComplete(pendingDynamicLinkData.link)
                }
            }*/
            .addOnFailureListener(activity) { e ->
                onComplete(null)
            }
    }

}