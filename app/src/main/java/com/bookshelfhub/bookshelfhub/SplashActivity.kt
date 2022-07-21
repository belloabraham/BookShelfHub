package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepo: IUserRepo
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var dynamicLink: IDynamicLink

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableFullScreenDisplayForSplashActivity(window)

        if (userAuth.getIsUserAuthenticated()){

            val userId = userAuth.getUserId()

            lifecycleScope.launch {
                    val user = userRepo.getUser(userId)

                    val nextIntent = if (user.isPresent && userId == user.get().userId)
                        Intent(this@SplashActivity, MainActivity::class.java)
                    else Intent(this@SplashActivity, WelcomeActivity::class.java)

                    getCollaboratorOrUserReferralLink(nextIntent)
            }

        }else{
            val intent = Intent(this, WelcomeActivity::class.java)
            getCollaboratorOrUserReferralLink(intent)
        }
    }


    @Suppress("DEPRECATION")
    private fun enableFullScreenDisplayForSplashActivity(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun getCollaboratorOrUserReferralLink(intent:Intent){
        var aCollaboratorOrUserReferralId:String?=null

        dynamicLink.getDeepLinkFromDynamicLinkAsync(this){
            val userLaunchedAppByClickingALink = it!=null
            if(userLaunchedAppByClickingALink){
                val deeplinkDomainPrefix = String.format(getString(R.string.dlink_deeplink_domain),"").trim()

                aCollaboratorOrUserReferralId = it.toString().replace(deeplinkDomainPrefix,"").trim()

                startNextActivity(intent, aCollaboratorOrUserReferralId)
            }else{
                startNextActivity(intent, aCollaboratorOrUserReferralId)
            }
        }

    }

    private fun startNextActivity(intent:Intent, aCollaboratorOrUserReferralId:String?){
        intent.putExtra(Referrer.ID, aCollaboratorOrUserReferralId)
        finish()
        startActivity(intent)
    }


}