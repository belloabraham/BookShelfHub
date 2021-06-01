package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bookshelfhub.bookshelfhub.R

class IntentUtil(private val context:Context) {


    fun openAppStoreIntent(appId:String):Intent{
        val intent = intent(R.string.uri_play_store_app, appId)
        return if (intent.resolveActivity(context.packageManager) != null) {
            intent
        } else {
            intent(R.string.uri_play_store_app_website, appId)
        }
    }

    fun intent(url: String): Intent {
        return intent(uri(url))
    }

    fun uri(url: String): Uri {
        return Uri.parse(url)
    }

    private fun intent(res: Int, user: String): Intent {
        return intent(uri(res, user))
    }

    fun intent(uri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, uri)
    }

    private fun uri(res: Int, user: String): Uri {
        return Uri.parse(context.getString(res, user))
    }

}