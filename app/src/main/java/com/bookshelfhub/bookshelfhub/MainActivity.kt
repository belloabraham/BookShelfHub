package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.adapters.viewpager.ViewPagerAdapter
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Social
import com.bookshelfhub.bookshelfhub.helpers.google.InAppUpdate
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.ui.main.BookmarkFragment
import com.bookshelfhub.bookshelfhub.ui.main.MoreFragment
import com.bookshelfhub.bookshelfhub.ui.main.ShelfFragment
import com.bookshelfhub.bookshelfhub.ui.main.StoreFragment
import com.bookshelfhub.bookshelfhub.workers.RecommendedBooks
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.InstallStatus
import com.google.common.base.Optional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var remoteConfig: IRemoteConfig

    @Inject
    lateinit var settingsUtil: SettingsUtil

    @Inject
    lateinit var dynamicLink: IDynamicLink

    @Inject
    lateinit var connectionUtil: ConnectionUtil

    @Inject
    lateinit var userAuth: IUserAuth
    private lateinit var inAppUpdate: InAppUpdate
    private val IN_APP_UPDATE_ACTIVITY_REQUEST_CODE = 700
    private var referrer: String? = null

    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = userAuth.getUserId()

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        //Check if there is an update for this app using In App update
        inAppUpdate = InAppUpdate(this)
        inAppUpdate.checkForNewAppUpdate { isImmediateUpdate, appUpdateInfo ->

            //Notify the more fragment update button of new update
            mainActivityViewModel.setIsNewUpdate()

            if (isImmediateUpdate) {
                inAppUpdate.startImmediateUpdate(appUpdateInfo, IN_APP_UPDATE_ACTIVITY_REQUEST_CODE)
            } else {
                inAppUpdate.startFlexibleUpdate(
                    appUpdateInfo,
                    IN_APP_UPDATE_ACTIVITY_REQUEST_CODE
                ) { installState ->
                    if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                        newUpdateInstallUpdateMessage()
                    }
                }
            }
        }

        //Get Nullable referral userID or PubIdAndISBN
        referrer = mainActivityViewModel.getReferrer()

        openReferrerLinkInStore(referrer)

        openReferrerLinkInStore(referrer)

        //Pre generate dynamic link before user request on app share to decrease share sheet load time
        getBookShareReferralLink(userId) {
            it?.let {
                val link = it.toString()
                mainActivityViewModel.setUserReferralLink(link)
                lifecycleScope.launch(IO) {
                    settingsUtil.setString(Referrer.REF_LINK, link)
                }
            }
        }

        mainActivityViewModel.getSelectedIndex().observe(this, Observer {
            //Navigate to a particular tab based on set value
            layout.bottomBar.selectTabAt(it, true)
        })

        mainActivityViewModel.getIsNewMoreTabNotif().observe(this, Observer {
            val moreTabIndex = 3
            val notifNumber = mainActivityViewModel.getTotalMoreTabNotification()

            if (notifNumber > 0) {
                //there is a notification, set notification bubble for the
                // bottom tab of more with the number of notification
                layout.bottomBar.setBadgeAtTabIndex(
                    moreTabIndex,
                    AnimatedBottomBar.Badge("$notifNumber")
                )
            } else {
                //there are no notification remove any notification badge if there is one
                layout.bottomBar.clearBadgeAtTabIndex(moreTabIndex)
            }
        })

        mainActivityViewModel.getBookInterest().observe(this, Observer { bookInterest ->
            mainActivityViewModel.updatedRecommendedBooks(bookInterest)
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

                if (newIndex > 1) {
                    //if user navigates to bookmark or more fragment
                    layout.shelfStoreViewPager.visibility = View.INVISIBLE
                    layout.cartMoreViewPager.visibility = VISIBLE
                    //set active visible view pager to get the last active view pager in the case of activity recreate when theme changes
                    mainActivityViewModel.setActiveViewPager(1)
                } else {
                    layout.shelfStoreViewPager.visibility = VISIBLE
                    layout.cartMoreViewPager.visibility = View.INVISIBLE
                    mainActivityViewModel.setActiveViewPager(0)
                }

                when (newIndex) {
                    0 -> {
                        //set active page to get the last active page in the case of activity recreate when theme changes
                        mainActivityViewModel.setActivePage(0)
                        layout.shelfStoreViewPager.setCurrentItem(0, true)
                    }
                    1 -> {
                        mainActivityViewModel.setActivePage(1)
                        layout.shelfStoreViewPager.setCurrentItem(1, true)
                    }
                    2 -> {
                        mainActivityViewModel.setActivePage(0)
                        layout.cartMoreViewPager.setCurrentItem(0, true)
                    }
                    3 -> {
                        mainActivityViewModel.setActivePage(1)
                        layout.cartMoreViewPager.setCurrentItem(1, true)
                    }
                }

            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
            }
        })

        mainActivityViewModel.getIsNightMode().observe(this, Observer { isDarkTheme ->
            setAppThem(isDarkTheme)
        })

    }

    private fun setAppThem(isDarkTheme: Boolean) {
        //change activity theme when user switch the theme in more fragment
        val mode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun newUpdateInstallUpdateMessage() {
        Snackbar.make(
            layout.container,
            getString(R.string.new_update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.install)) {
                inAppUpdate.installNewUpdate()
            }
            show()
        }
    }

    override fun onResume() {
        super.onResume()

        //Check if user has a completed download to prompt them for install
        inAppUpdate.checkForDownloadedOrDownloadingUpdate(IN_APP_UPDATE_ACTIVITY_REQUEST_CODE) {
            newUpdateInstallUpdateMessage()
        }

        setActiveViewPagerAndPageAfterMainActivityThemeChange()

    }

    private fun setActiveViewPagerAndPageAfterMainActivityThemeChange() {
        //Set active view pager and page based on what was last set by bottom nav on select change  to saved state Instance as a result of activity recreated from theme change, as activity recreated create the effect of when resource get reclaimed by the OS
        if (mainActivityViewModel.getActiveViewPager() == 0) {
            //Programmatic select tab will only will only switch view pager in onResume and not on create when activity recreated from theme changed
            layout.bottomBar.selectTabAt(
                if (mainActivityViewModel.getActivePage() == 0) 0 else 1,
                true
            )
            layout.shelfStoreViewPager.visibility = VISIBLE
            layout.cartMoreViewPager.visibility = View.INVISIBLE
            layout.shelfStoreViewPager.currentItem = mainActivityViewModel.getActivePage()!!
        } else if (mainActivityViewModel.getActiveViewPager() == 1) {
            layout.bottomBar.selectTabAt(
                if (mainActivityViewModel.getActivePage() == 0) 2 else 3,
                true
            )
            layout.cartMoreViewPager.currentItem = mainActivityViewModel.getActivePage()!!
            layout.shelfStoreViewPager.visibility = View.INVISIBLE
            layout.cartMoreViewPager.visibility = VISIBLE
        }
    }

    override fun onBackPressed() {
        //Workaround to android 10 leak when user press back button on main activity
        //This code prevents the leak
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (onBackPressedDispatcher.hasEnabledCallbacks()) {
                super.onBackPressed()
            } else {
                finishAfterTransition()
            }
        } else {
            super.onBackPressed()
        }
    }


    private fun openReferrerLinkInStore(referrer: String?) {
        referrer?.let {
            //referrer will not be null if referrer exist
            val ref = it
            //Check if a publisher refer the user
            if (ref.length > userId.length) {

                val pubIdAndIsbn = ref.split(Referrer.SEPARATOR)
                val publisherId = pubIdAndIsbn[0]
                val isbn = pubIdAndIsbn[1]
                val intent = Intent(this, BookItemActivity::class.java)
                intent.putExtra(Referrer.BOOK_REFERRED, isbn)
                val pubRefRecord = PubReferrers(publisherId, isbn)
                //Add publisher ID that refer the user to the database
                mainActivityViewModel.addPubReferrer(pubRefRecord)
                //opened the book that the publisher refer in Book Store
                startActivity(intent)
            }
        }
    }

    private fun getBookShareReferralLink(userId: String, onComplete: (Uri?) -> Unit) {
        val title = remoteConfig.getString(Social.TITLE)
        val description = remoteConfig.getString(Social.DESC)
        val imageUrl = remoteConfig.getString(Social.IMAGE_URL)
        dynamicLink.generateShortLinkAsync(title, description, imageUrl, userId) {
            onComplete(it)
        }
    }

    private fun setUpShelfStoreViewPager() {
        val fragmentList = listOf(ShelfFragment.newInstance(), StoreFragment.newInstance())
        val shelfStoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        layout.shelfStoreViewPager.isUserInputEnabled = false
        layout.shelfStoreViewPager.adapter = shelfStoreAdapter
    }

    private fun setUpCartMoreViewPager() {
        val fragmentList = listOf(BookmarkFragment.newInstance(), MoreFragment.newInstance())
        val cartMoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        layout.cartMoreViewPager.isUserInputEnabled = false
        layout.cartMoreViewPager.adapter = cartMoreAdapter
    }

    override fun onDestroy() {
        layout.shelfStoreViewPager.adapter = null
        layout.cartMoreViewPager.adapter = null
        super.onDestroy()
    }

}