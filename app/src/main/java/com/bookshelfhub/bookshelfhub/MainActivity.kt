package com.bookshelfhub.bookshelfhub

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.adapters.main.CartMorePagerAdapter
import com.bookshelfhub.bookshelfhub.adapters.main.ShelfStorePagerAdapter
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.enums.PubReferrer
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.enums.UserReferrer
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.helpers.MaterialDialogHelper
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.ui.main.CartFragment
import com.bookshelfhub.bookshelfhub.ui.main.MoreFragment
import com.bookshelfhub.bookshelfhub.ui.main.ShelfFragment
import com.bookshelfhub.bookshelfhub.ui.main.StoreFragment
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.bookshelfhub.bookshelfhub.wrapper.dynamiclink.DynamicLink
import com.google.android.material.progressindicator.LinearProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar
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
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var dynamicLink: DynamicLink

    private lateinit var userId:String
    private val ENFORCE_UPDATE="enforce_update"
    private val CHANGE_LOG="change_log"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId=mainActivityViewModel.getUserId()

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        getUserReferrerLinkAsync(userId){
            it?.let {
                val link = it.toString()
                mainActivityViewModel.setUserReferralLink(link)
                lifecycleScope.launch(IO){
                    settingsUtil.setString(PubReferrer.USER_REF_LINK.KEY,link)
                }
            }
        }

        mainActivityViewModel.getSelectedIndex().observe(this, Observer {
            layout.bottomBar.selectTabAt(it, true)
        })

        mainActivityViewModel.getIsNewProfileNotif().observe(this, Observer { isNewProfileNotif ->

            val profileTabIndex = 3
            val notifNumber = mainActivityViewModel.getTotalProfileNotifNumber()

            if (notifNumber>0){
                layout.bottomBar.setBadgeAtTabIndex(profileTabIndex, AnimatedBottomBar.Badge("${notifNumber}"))
            }else{
                layout.bottomBar.clearBadgeAtTabIndex(profileTabIndex)
            }

        })

        mainActivityViewModel.getUserRecord().observe(this, Observer { userRecord ->
           /* if (userRecord.mailOrPhoneVerified){
                mainActivityViewModel.setVerifyPhoneOrEmailNotif(0)
            }else{
                mainActivityViewModel.setVerifyPhoneOrEmailNotif(1)
            }*/
        })



        mainActivityViewModel.getBookInterest().observe(this, Observer { bookInterest ->
            if(bookInterest.isPresent && bookInterest.get().added){
                mainActivityViewModel.setBookInterestNotifNo(0)
            }else{
                mainActivityViewModel.setBookInterestNotifNo(1)
            }
        })


        mainActivityViewModel.getIsUpdateAvailable().observe(this, Observer { updateAvailable ->
            if (updateAvailable){
               showNewUpdateAlertAndNotification()
            }
        })

        setUpShelfStoreViewPager()
        setUpCartMoreViewPager()

        layout.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {

                if (newIndex>1){
                    layout.shelfStoreViewPager.visibility=View.INVISIBLE
                    layout.cartMoreViewPager.visibility=View.VISIBLE
                }else{
                    layout.shelfStoreViewPager.visibility=View.VISIBLE
                    layout.cartMoreViewPager.visibility=View.INVISIBLE
                }
                when(newIndex){
                    0->
                       layout.shelfStoreViewPager.setCurrentItem(0, true)
                    1->
                        layout.shelfStoreViewPager.setCurrentItem(1, true)
                    2->
                        layout.cartMoreViewPager.setCurrentItem(0, true)
                    3->
                        layout.cartMoreViewPager.setCurrentItem(1, true)
                }
            }
            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
            }
        })


        mainActivityViewModel.getIsNightMode().observe(this, Observer { isNightMode ->
            layout.shelfStoreViewPager.currentItem=0
            val mode = if (isNightMode){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        })

        showProgressPopupDialog()

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
        AlertDialogHelper(this,{
            startActivity(intentUtil.openAppStoreIntent(packageName))
        },{
            if(enforceUpdate){
                finish()
            }
        }).showAlertDialog(title,msg,positiveActionText,negActionText)

    }

    private fun showProgressPopupDialog(){
        lifecycleScope.launch(IO) {
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            val lastBookRedTile = settingsUtil.getString(Settings.LAST_BOOK_RED_TITLE.KEY)
            val lastBookRedISBN = settingsUtil.getString(Settings.LAST_BOOK_RED_ISBN.KEY)
            val lastBookPercentage = settingsUtil.getInt(Settings.LAST_BOOK_PERCENTAGE.KEY, 0)
            val noOfDismiss = settingsUtil.getInt(Settings.NO_OF_TIME_DISMISSED.KEY, 0)
            withContext(Main){
                if (showPopup){
                    lastBookRedTile?.let {
                        val view = View.inflate(this@MainActivity, R.layout.continue_reading, null)
                        view.findViewById<TextView>(R.id.bookName).text = it
                        view.findViewById<TextView>(R.id.percentageText).text = String.format(getString(R.string.percent), lastBookPercentage)
                        view.findViewById<LinearProgressIndicator>(R.id.progressIndicator).progress = lastBookPercentage

                        MaterialDialogHelper(this@MainActivity, this@MainActivity, {
                            if (noOfDismiss<2){
                                Toast(this@MainActivity).showToast(R.string.dismiss_msg)
                                runBlocking {
                                    settingsUtil.setInt(Settings.NO_OF_TIME_DISMISSED.KEY, noOfDismiss+1)
                                }
                            }
                        }, {
                                val intent = Intent(this@MainActivity, ContentActivity::class.java)
                                intent.putExtra(Settings.LAST_BOOK_RED_ISBN.KEY, lastBookRedISBN)
                                startActivity(intent)
                            }
                        )
                            .showBottomSheet(view, R.string.dismiss, R.string.continue_reading)
                    }
                }
            }
        }
    }


    private fun getReferrer(referrer:String?, userId:String){
        referrer?.let {
            if (it.length>userId.length){
               val pubIdAndIsbn = it.split("@")
                val publisherId = pubIdAndIsbn[0]
                val isbn = pubIdAndIsbn[1]
                val intent = Intent(this, BookItemActivity::class.java)
                intent.putExtra(PubReferrer.ISBN.KEY,isbn)
                val pubRefRecord = PubReferrers(publisherId, isbn)
                mainActivityViewModel.addPubReferrer(pubRefRecord)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val referrer = intent.getStringExtra(PubReferrer.ID.KEY)
        getReferrer(referrer, userId)
    }

    private fun getUserReferrerLinkAsync(userId:String, onComplete:(Uri?)->Unit){
        val title = remoteConfig.getString(UserReferrer.USER_REF_TITLE.KEY)
        val description = remoteConfig.getString(UserReferrer.USER_REF_DESC.KEY)
        val imageUrl = remoteConfig.getString(UserReferrer.USER_REF_IMAGE_URI.KEY)
        dynamicLink.getLinkAsync(title, description, imageUrl, userId){
            onComplete(it)
        }
    }


    private fun setUpShelfStoreViewPager(){
        val  fragmentList = listOf(ShelfFragment.newInstance(), StoreFragment.newInstance())
        val titles = arrayOf(getString(R.string.shelf), getString(R.string.store))
        val shelfStoreAdapter = ShelfStorePagerAdapter(supportFragmentManager, fragmentList, titles)
        layout.shelfStoreViewPager.adapter = shelfStoreAdapter
    }

    private fun setUpCartMoreViewPager(){
        val  fragmentList = listOf(CartFragment.newInstance(), MoreFragment.newInstance())
        val titles = arrayOf(getString(R.string.cart), getString(R.string.more))
        val cartMoreAdapter = CartMorePagerAdapter( supportFragmentManager, fragmentList, titles)
        layout.cartMoreViewPager.adapter = cartMoreAdapter
    }

}