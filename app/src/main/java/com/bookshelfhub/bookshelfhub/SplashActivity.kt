package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepo: UserRepo
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var dynamicLink: IDynamicLink


    override fun onStart() {
        super.onStart()

        // Enable full screen display ***/
        hideSystemUI(window)

        // Check if user signed in ***/
        if (userAuth.getIsUserAuthenticated()){
            val userId = userAuth.getUserId()

            lifecycleScope.launch {
                // Get user data***/
                val user = userRepo.getUser(userId)

                withContext(Main){
                    // Check if user data exist as user may not complete sign up which requires user data***/
                    val intent = if (user.isPresent && userId == user.get().userId){
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }else{
                        // If user data does not exist but user signed in take user to Welcome screen to complete ***
                        // sign in by entering there data***
                        Intent(this@SplashActivity, WelcomeActivity::class.java)
                    }

                    getCollaboratorOrUserReferralLink(intent)
                }
            }
        }else{
            // Take user to welcome screen as user is yet to sign in
            val intent = Intent(this, WelcomeActivity::class.java)
            getCollaboratorOrUserReferralLink(intent)
        }

    }

    @Suppress("DEPRECATION")
    private fun hideSystemUI(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun getCollaboratorOrUserReferralLink(intent:Intent){
        // This App could've been opened by a dynamic link and not the from the app icon
        var referrer:String?=null

        dynamicLink.getDeepLinkAsync(this){
            if(it!=null){
                // Get deep link main url
                val deeplinkDomainPrefix = String.format(getString(R.string.dlink_deeplink_domain),"").trim()
                //  Remove the main url to get referral userID or PubIdAndISBN
                referrer = it.toString().replace(deeplinkDomainPrefix,"").trim()

                //  Start Main or Welcome or Main Activity with referral userID or PubIdAndISBN
                startNextActivity(intent, referrer)
            }else{
                //  Start Main or Welcome or Main Activity with a null referral userID or PubIdAndISBN
                startNextActivity(intent, referrer)
            }
        }

    }

    private fun startNextActivity(intent:Intent, referrerId:String?){
        intent.putExtra(Referrer.ID, referrerId)
        finish()
        startActivity(intent)
    }


}