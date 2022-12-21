package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.bookshelfhub.core.authentication.IUserAuth
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.dynamic_link.IDynamicLink
import com.bookshelfhub.core.dynamic_link.Referrer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bookshelfhub.feature.onboarding.WelcomeActivity
import com.bookshelfhub.feature.home.MainActivity
import com.bookshelfhub.core.resources.R


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

        val intent = Intent(this, WelcomeActivity::class.java)

        if  (userAuth.getIsUserAuthenticated()){
            val userId = userAuth.getUserId()

            lifecycleScope.launch {
                    val user = userRepo.getUser(userId)
                    val nextIntent = if (user.isPresent && userId == user.get().userId){
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }else {
                        intent
                    }
                    getCollaboratorOrUserReferralLink(nextIntent)
            }

        }else{
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
        dynamicLink.getDeepLinkFromDynamicLinkAsync(this){ uri ->

                val userLaunchedAppByClickingALink = uri != null
                if(userLaunchedAppByClickingALink){

                    val domainPrefixLen = getString(R.string.replace_dlink_deeplink_domain).length
                    val stringUri = uri.toString()
                    aCollaboratorOrUserReferralId = getUriData(stringUri, domainPrefixLen)

                    startNextActivityWith(intent, aCollaboratorOrUserReferralId)

                }else{
                    startNextActivityWith(intent, aCollaboratorOrUserReferralId)
                }
        }

    }


    private  fun getUriData(uri:String, startIndex:Int): String {
        val result = StringBuilder()
        var index = 0
        for (char in uri){
            if(index >= startIndex){
                result.append(char)
            }
            index++
        }
        return result.toString()
    }


    private fun startNextActivityWith(intent:Intent, aCollaboratorOrUserReferralId:String?){
        intent.putExtra(Referrer.ID, aCollaboratorOrUserReferralId)
        finish()
        startActivity(intent)
    }


}