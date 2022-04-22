package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.bookshelfhub.bookshelfhub.R
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Firebase @Inject constructor(private val domainPrefix:String, private val context:Context) :
    IDynamicLink {

    override suspend fun generateShortDynamicLinkAsync(socialTitle:String,
                                                       socialDescription:String,
                                                       imageUri:String,
                                                       uniqueId: String,
                                                       minimumVCode:Int): Uri? {

         return withContext(IO){ val uri = Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {

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
                imageUrl = Uri.parse(imageUri)
            }
        }.await()
         uri.shortLink}
    }

    /**
     * Used to get the link deep link from the generated Link
     */
    override fun getDeepLinkFromDynamicLinkAsync(activity:Activity, onComplete:(Uri?)->Unit){
            Firebase.dynamicLinks
                .getDynamicLink(activity.intent)
                .addOnSuccessListener(activity) { pendingDynamicLinkData:PendingDynamicLinkData? ->
                    onComplete(pendingDynamicLinkData?.link)
                }
                .addOnFailureListener(activity) {
                    onComplete(null)
                }
    }

}