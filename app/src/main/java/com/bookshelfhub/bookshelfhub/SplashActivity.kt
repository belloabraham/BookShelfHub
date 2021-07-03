package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.enums.Referrer
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.wrapper.dynamiclink.DynamicLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var localDb: LocalDb
    @Inject
    lateinit var userAuth: UserAuth
    @Inject
    lateinit var dynamicLink: DynamicLink

    override fun onCreate(savedInstanceState: Bundle?) {
       hideSystemUI(window)
        super.onCreate(savedInstanceState)

        if (userAuth.getIsUserAuthenticated()){
            lifecycleScope.launch(IO) {
                val user = localDb.getUser(userAuth.getUserId())
              withContext(Main){
                 val intent = if (user.isPresent && userAuth.getUserId() == user.get().userId){
                      Intent(this@SplashActivity, MainActivity::class.java)
                  }else{
                      Intent(this@SplashActivity, WelcomeActivity::class.java)
                  }
                  getReferrer(intent)
              }
            }
        }else{
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
        val referralRecord = localDb.getReferrer()
        var referrer:String?=null
        if (!referralRecord.isPresent){
            dynamicLink.getDeepLinkAsync(this){
                if(it!=null){
                    val deeplinkDomainPrefix = String.format(getString(R.string.dlink_deeplink_domain),"").trim()
                      referrer = it.toString().replace(deeplinkDomainPrefix,"").trim()
                    startNextActivity(intent, referrer)

                }else{
                    startNextActivity(intent, referrer)
                }
            }
        }else{
            startNextActivity(intent, referrer)
        }
    }

    private fun startNextActivity(intent:Intent, referrerId:String?){
        intent.putExtra(Referrer.ID.KEY, referrerId)
        finish()
        startActivity(intent)
    }

}