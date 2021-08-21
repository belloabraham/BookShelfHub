package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.enums.PubReferrer
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.IDynamicLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var dynamicLink: IDynamicLink

    override fun onStart() {
        super.onStart()

        //***Enable full screen display ***/
        hideSystemUI(window)


        //***Check if user signed in ***/
        if (userAuth.getIsUserAuthenticated()){
            lifecycleScope.launch(IO) {

                //***Get user data***/
                val user = localDb.getUser(userAuth.getUserId())

                withContext(Main){
                    //***Check if user data exist as user may not complete sign up which requires user data***/
                    val intent = if (user.isPresent && userAuth.getUserId() == user.get().userId){
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }else{
                        //***If user data does not exist but user signed in take user to Welcome screen to complete ***
                        //***sign in by entering there data***
                        Intent(this@SplashActivity, WelcomeActivity::class.java)
                    }

                    getReferrer(intent)
                }
            }
        }else{
            //***Take user to welcome screen as user is yet to sign in
            val intent = Intent(this, WelcomeActivity::class.java)
            getReferrer(intent)
        }

    }

    private fun hideSystemUI(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun getReferrer(intent:Intent){
        //***Get referral deep link that could be null or from a User or a Publisher referring a book**
        var referrer:String?=null
        dynamicLink.getDeepLinkAsync(this){
            if(it!=null){
                //*** Get deep link main url
                val deeplinkDomainPrefix = String.format(getString(R.string.dlink_deeplink_domain),"").trim()
                //*** Remove the main url to get referral userID or PubIdAndISBN
                referrer = it.toString().replace(deeplinkDomainPrefix,"").trim()

                //*** Start Main or Welcome Activity with a null referral userID or PubIdAndISBN
                startNextActivity(intent, referrer)
            }else{
                //*** Start Main or Welcome Activity with a null referral userID or PubIdAndISBN
                startNextActivity(intent, referrer)
            }
        }
    }

    private fun startNextActivity(intent:Intent, referrerId:String?){
        intent.putExtra(PubReferrer.ID.KEY, referrerId)
        finish()
        startActivity(intent)
    }

}