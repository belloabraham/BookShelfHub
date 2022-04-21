package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.adapters.viewpager.ViewPagerAdapter
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.google.InAppUpdate
import com.bookshelfhub.bookshelfhub.ui.main.BookmarkFragment
import com.bookshelfhub.bookshelfhub.ui.main.MoreFragment
import com.bookshelfhub.bookshelfhub.ui.main.ShelfFragment
import com.bookshelfhub.bookshelfhub.ui.main.StoreFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private lateinit var inAppUpdate: InAppUpdate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        //Check if there is an update for this app using In App update
        inAppUpdate = InAppUpdate(this)
        checkForNewAppUpdate(inAppUpdate)


        mainActivityViewModel.getBookIdFromACollaboratorReferrerId()?.let { bookId->
           openBookInBookStore(bookId)
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

                setViewPagerPosition(newIndex)

            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {}
        })

    }

    private fun setViewPagerPosition(tabIndex:Int){
        when (tabIndex) {
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


    private fun checkForNewAppUpdate(inAppUpdate:InAppUpdate){
        inAppUpdate.checkForNewAppUpdate { isImmediateUpdate, appUpdateInfo ->

            //Notify the more fragment update button of new update
            mainActivityViewModel.setIsNewUpdate()

            if (isImmediateUpdate) {
                inAppUpdate.startImmediateUpdate(appUpdateInfo, InAppUpdate.ACTIVITY_REQUEST_CODE)
            } else {
                inAppUpdate.startFlexibleUpdate(
                    appUpdateInfo,
                    InAppUpdate.ACTIVITY_REQUEST_CODE
                ) { installState ->
                    if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                        newUpdateInstallUpdateMessage()
                    }
                }
            }
        }
    }

    private fun openBookInBookStore(bookId:String){
        val intent = Intent(this, BookItemActivity::class.java)
        intent.putExtra(Referrer.BOOK_REFERRED, bookId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getAndSaveAppShareDynamicLink()

        //Check if user has a completed download to prompt them for install
        inAppUpdate.checkForDownloadedOrDownloadingUpdate(InAppUpdate.ACTIVITY_REQUEST_CODE) {
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
                false
            )
            layout.shelfStoreViewPager.visibility = VISIBLE
            layout.cartMoreViewPager.visibility = INVISIBLE
            layout.shelfStoreViewPager.currentItem = mainActivityViewModel.getActivePage()!!
        } else if (mainActivityViewModel.getActiveViewPager() == 1) {
            layout.bottomBar.selectTabAt(
                if (mainActivityViewModel.getActivePage() == 0) 2 else 3,
                false
            )
            layout.cartMoreViewPager.currentItem = mainActivityViewModel.getActivePage()!!
            layout.shelfStoreViewPager.visibility = INVISIBLE
            layout.cartMoreViewPager.visibility = VISIBLE
        }
    }

    override fun onBackPressed() {
        //Workaround to android 10 leak when user press back button on main activity
        //This code prevents the leak
        val deviceOSIsAndroid10 = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
        if (deviceOSIsAndroid10) {
            if (onBackPressedDispatcher.hasEnabledCallbacks()) {
                super.onBackPressed()
            } else {
                finishAfterTransition()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun setUpShelfStoreViewPager() {
        val fragmentList = listOf(ShelfFragment.newInstance(), StoreFragment.newInstance())
        val shelfStoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        //Disables Swipe left and right
        layout.shelfStoreViewPager.isUserInputEnabled = false
        layout.shelfStoreViewPager.adapter = shelfStoreAdapter
    }

    private fun setUpCartMoreViewPager() {
        val fragmentList = listOf(BookmarkFragment.newInstance(), MoreFragment.newInstance())
        val cartMoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        //Disables Swipe left and right
        layout.cartMoreViewPager.isUserInputEnabled = false
        layout.cartMoreViewPager.adapter = cartMoreAdapter
    }

    override fun onDestroy() {
        layout.shelfStoreViewPager.adapter = null
        layout.cartMoreViewPager.adapter = null
        super.onDestroy()
    }

}