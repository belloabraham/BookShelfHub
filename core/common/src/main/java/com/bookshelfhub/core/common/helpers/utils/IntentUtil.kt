package com.bookshelfhub.core.common.helpers.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import com.bookshelfhub.core.resources.R


@Suppress("DEPRECATION")
class IntentUtil(private val context:Context) {

    fun getAppStoreIntent(appId:String):Intent{
        val intent = intent(R.string.uri_play_store_app, appId)
        return if (intent.resolveActivity(context.packageManager) != null) {
            intent
        } else {
            intent(R.string.uri_play_store_app_website, appId)
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun tryPackage(res: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            context.packageManager.getPackageInfo(context.getString(res), PackageManager.PackageInfoFlags.of(0))
        }else{
            context.packageManager.getPackageInfo(context.getString(res), 0)
        }
    }

    fun clickUri(uri: Uri): View.OnClickListener {
        return clickIntent(intent(uri))
    }

    fun clickIntent(intent: Intent): View.OnClickListener {
        return View.OnClickListener { open(intent) }
    }

    private fun open(intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun open(uri: Uri) {
        open(intent(uri))
    }

    fun openPhoneDialer(phone:String):Intent{
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phone.trim()}")
        return intent
    }

    fun openInstagram(user: String): Intent {
        return try {
            tryPackage(R.string.id_instagram_app)
            intent(R.string.uri_instagram_app, user)
        } catch (e: Exception) {
            intent(R.string.url_instagram_website, user)
        }
    }

    fun sendEmail(email: String, subject: String?, message: String?): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        return intent
    }

    fun openFacebook(user: String): Intent {
        return try {
            tryPackage(R.string.id_facebook_app)
            intent(R.string.uri_facebook_app, user)
        } catch (e: java.lang.Exception) {
            intent(R.string.url_facebook_website, user)
        }
    }

    fun openTwitter(user: String): Intent {
        return intent(R.string.url_twitter_website, user)
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