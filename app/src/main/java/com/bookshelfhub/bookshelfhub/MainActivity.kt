package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
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

        inAppUpdate = InAppUpdate(this)
        checkForNewAppUpdate(inAppUpdate)


        mainActivityViewModel.getBookIdFromACollaboratorReferrerId()?.let { bookId->
           openBookInBookStore(bookId)
        }

        mainActivityViewModel.getSelectedIndex().observe(this, Observer {
            layout.bottomBar.selectTabAt(it, true)
        })

        mainActivityViewModel.getIsNewMoreTabNotif().observe(this, Observer {
            val moreTabIndex = 3
            val notifNumber = mainActivityViewModel.getTotalMoreTabNotification()

            if (notifNumber > 0) {
                layout.bottomBar.setBadgeAtTabIndex(
                    moreTabIndex,
                    AnimatedBottomBar.Badge("$notifNumber")
                )
            } else {
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
                    layout.shelfStoreViewPager.visibility = INVISIBLE
                    layout.cartMoreViewPager.visibility = VISIBLE
                    mainActivityViewModel.setActiveViewPager(1)
                } else {
                    layout.shelfStoreViewPager.visibility = VISIBLE
                    layout.cartMoreViewPager.visibility = INVISIBLE
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

    private fun newUpdateDownloadedCompleteMessage() {
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

            mainActivityViewModel.setIsNewUpdate()

            if (isImmediateUpdate) {
                inAppUpdate.startImmediateUpdate(appUpdateInfo, InAppUpdate.ACTIVITY_REQUEST_CODE)
            } else {
                inAppUpdate.startFlexibleUpdate(
                    appUpdateInfo,
                    InAppUpdate.ACTIVITY_REQUEST_CODE
                ) { installState ->
                    if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                        newUpdateDownloadedCompleteMessage()
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


    override fun onBackPressed() {
        val deviceOSIsAndroid10 = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
        if (deviceOSIsAndroid10) {
            exitAppWithoutMemoryLeakOnAndroid10()
        } else {
            super.onBackPressed()
        }
    }

    private fun exitAppWithoutMemoryLeakOnAndroid10(){
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            super.onBackPressed()
        } else {
            finishAfterTransition()
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getAndSaveAppShareDynamicLink()

        inAppUpdate.checkForDownloadedOrDownloadingUpdate(InAppUpdate.ACTIVITY_REQUEST_CODE) {
            newUpdateDownloadedCompleteMessage()
        }

        setActiveViewPagerAndPageAfterMainActivityThemeChange()
    }

    private fun setActiveViewPagerAndPageAfterMainActivityThemeChange() {
        if (mainActivityViewModel.getActiveViewPager() == 0) {
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


    private fun setUpShelfStoreViewPager() {
        val fragmentList = listOf(ShelfFragment.newInstance(), StoreFragment.newInstance())
        val shelfStoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        //Disables Swipe left and right for viewpager
        layout.shelfStoreViewPager.isUserInputEnabled = false
        layout.shelfStoreViewPager.adapter = shelfStoreAdapter
    }

    private fun setUpCartMoreViewPager() {
        val fragmentList = listOf(BookmarkFragment.newInstance(), MoreFragment.newInstance())
        val cartMoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        //Disables Swipe left and right for viewpager
        layout.cartMoreViewPager.isUserInputEnabled = false
        layout.cartMoreViewPager.adapter = cartMoreAdapter
    }

    override fun onDestroy() {
        layout.shelfStoreViewPager.adapter = null
        layout.cartMoreViewPager.adapter = null
        super.onDestroy()
    }

}