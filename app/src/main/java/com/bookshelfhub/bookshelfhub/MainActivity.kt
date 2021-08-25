package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.adapters.viewpager.CartMorePagerAdapter
import com.bookshelfhub.bookshelfhub.adapters.viewpager.ShelfStorePagerAdapter
import com.bookshelfhub.bookshelfhub.config.IRemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.PubReferrer
import com.bookshelfhub.bookshelfhub.Utils.settings.Settings
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.ReferrerLink
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.MaterialBottomSheetBuilder
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.ui.main.BookmarkFragment
import com.bookshelfhub.bookshelfhub.ui.main.MoreFragment
import com.bookshelfhub.bookshelfhub.ui.main.ShelfFragment
import com.bookshelfhub.bookshelfhub.ui.main.StoreFragment
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.RecommendedBooks
import com.bookshelfhub.bookshelfhub.workers.UploadBookInterest
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.IDynamicLink
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
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var dynamicLink: IDynamicLink
    @Inject
    lateinit var userAuth: IUserAuth

    private lateinit var userId:String
    private val ENFORCE_UPDATE="enforce_update"
    private val CHANGE_LOG="change_log"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId=userAuth.getUserId()

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)


        //***Get Nullable referral userID or PubIdAndISBN and set to userAuthViewModel
        val referrer = intent.getStringExtra(PubReferrer.ID.KEY)

        //***Open dynamic link that opened this app in Book store if the link is not null and is coming from a publisherReferrer
        referrer?.let {
            if (it.length>userId.length){
                openPublisherReferrerLink(it, userId)
            }
        }

        //***Pre generate dynamic link before user request on app share to decrease share sheet load time
        getBookShareReferralLink(userId){
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
                layout.bottomBar.setBadgeAtTabIndex(profileTabIndex, AnimatedBottomBar.Badge("$notifNumber"))
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

                val recommendedBooksWorker: WorkRequest =
                    OneTimeWorkRequestBuilder<RecommendedBooks>()
                        .build()
                WorkManager.getInstance(this).enqueue(recommendedBooksWorker)

                val oneTimeBookInterestUpload: WorkRequest =
                    OneTimeWorkRequestBuilder<UploadBookInterest>()
                        .setConstraints(Constraint.getConnected())
                        .build()

                WorkManager.getInstance(this).enqueue(oneTimeBookInterestUpload)
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

        showReadProgressDialog()

    }

   private var isFirstStart = true
    override fun onResume() {

            if(isFirstStart){
              layout.shelfStoreViewPager.currentItem =0
              isFirstStart=false
            }

        mainActivityViewModel.getIsNightMode().observe(this, Observer { isNightMode ->

            val mode = if (isNightMode){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        })

        super.onResume()
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
        NotificationBuilder(this)
            .setTitle(title)
            .setUrl(packageName)
            .setMessage(msg)
            .build()
            .showNotification(R.integer.update_notif_id){
                intentUtil.openAppStoreIntent(this)
            }

        AlertDialogBuilder.with(this, msg).setPositiveAction(positiveActionText){
            startActivity(intentUtil.openAppStoreIntent(packageName))
        }.setNegativeAction(negActionText){
            if(enforceUpdate){
                finish()
            }
        }.build().showDialog(title)

    }


    private fun showReadProgressDialog() {
        lifecycleScope.launch(IO) {
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            val lastBookRedTile = settingsUtil.getString(Settings.LAST_BOOK_RED_TITLE.KEY)
            val lastBookRedISBN = settingsUtil.getString(Settings.LAST_BOOK_RED_ISBN.KEY)
            val lastBookPercentage = settingsUtil.getInt(Settings.LAST_BOOK_PERCENTAGE.KEY, 0)
            val noOfDismiss = settingsUtil.getInt(Settings.NO_OF_TIME_DISMISSED.KEY, 0)
            withContext(Main) {
                if (showPopup) {
                    lastBookRedTile?.let {
                        val view = View.inflate(this@MainActivity, R.layout.continue_reading, null)
                        view.findViewById<TextView>(R.id.bookName).text = it
                        view.findViewById<TextView>(R.id.percentageText).text =
                            String.format(getString(R.string.percent), lastBookPercentage)
                        view.findViewById<LinearProgressIndicator>(R.id.progressIndicator).progress =
                            lastBookPercentage

                        MaterialBottomSheetBuilder(this@MainActivity, this@MainActivity)
                            .setOnDismissListener {
                                if (noOfDismiss < 2) {
                                    showToast(R.string.dismiss_msg)
                                    runBlocking {
                                        settingsUtil.setInt(
                                            Settings.NO_OF_TIME_DISMISSED.KEY,
                                            noOfDismiss + 1
                                        )
                                    }
                                }
                            }
                            .setPositiveAction(R.string.dismiss) {}
                            .setNegativeAction(R.string.continue_reading) {
                                val intent = Intent(this@MainActivity, BookActivity::class.java)
                                intent.putExtra(Settings.LAST_BOOK_RED_ISBN.KEY, lastBookRedISBN)
                                startActivity(intent)
                            }
                            .showBottomSheet(view)
                    }
                }
            }
        }
    }

    private fun openPublisherReferrerLink(referrer:String, userId:String){
                val pubIdAndIsbn = referrer.split(PubReferrer.SEPARATOR.KEY)
                val publisherId = pubIdAndIsbn[0]
                val isbn = pubIdAndIsbn[1]
                val intent = Intent(this, BookItemActivity::class.java)
                intent.putExtra(PubReferrer.ISBN.KEY,isbn)
                val pubRefRecord = PubReferrers(publisherId, isbn)
                //***Add publisher referrer to the database
                mainActivityViewModel.addPubReferrer(pubRefRecord)
                startActivity(intent)
    }

    private fun getBookShareReferralLink(userId:String, onComplete:(Uri?)->Unit){
        val title = remoteConfig.getString(ReferrerLink.TITLE.KEY)
        val description = remoteConfig.getString(ReferrerLink.DESC.KEY)
        val imageUrl = remoteConfig.getString(ReferrerLink.IMAGE_URL.KEY)
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
        val  fragmentList = listOf(BookmarkFragment.newInstance(), MoreFragment.newInstance())
        val titles = arrayOf(getString(R.string.cart), getString(R.string.more))
        val cartMoreAdapter = CartMorePagerAdapter( supportFragmentManager, fragmentList, titles)
        layout.cartMoreViewPager.adapter = cartMoreAdapter
    }

}