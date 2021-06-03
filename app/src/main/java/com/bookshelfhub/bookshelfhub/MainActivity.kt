package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.Format
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMainBinding
    private val mainActivityViewModel:MainActivityViewModel by viewModels()
    @Inject
    lateinit var remoteConfig: RemoteConfig
    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var intentUtil: IntentUtil
    private val ENFORCE_UPDATE="enforce_update"
    private val CHANGE_LOG="change_log"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)


        mainActivityViewModel.getIsUpdateAvailable().observe(this, Observer { updateAvailable ->
            if (updateAvailable){
               showNewUpdateAlertAndNotification()
            }
        })

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

       /* val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/
    }


    private fun showNewUpdateAlertAndNotification(){
        val title:String
        val msg:String
        val negActionText:String
        val enforceUpdate = remoteConfig.getBoolean(ENFORCE_UPDATE)
        if  (enforceUpdate){
            title=getString(R.string.update_req_title)
            msg=getString(R.string.update_req_mgs)
            negActionText=getString(R.string.close)
        }else{
            val changeLog = remoteConfig.getString(CHANGE_LOG)
            title=getString(R.string.update_title)
            msg=String.format(getString(R.string.update_mgs), changeLog)
            negActionText=getString(R.string.later)
        }
        val positiveActionText=getString(R.string.update)
        notificationHelper.showNotification(title,msg,R.integer.update_notif_id,packageName,positiveActionText)
        AlertDialogHelper(this,title,msg).showAlertDialog(positiveActionText,{
            startActivity(intentUtil.openAppStoreIntent(packageName))
        },negActionText,{
            if(enforceUpdate){
                finish()
            }
        })
    }
}