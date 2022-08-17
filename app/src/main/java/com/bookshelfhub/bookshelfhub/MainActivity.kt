package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.adapters.viewpager.ViewPagerAdapter
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.google.InAppUpdate
import com.bookshelfhub.bookshelfhub.ui.main.bookmark.BookmarkFragment
import com.bookshelfhub.bookshelfhub.ui.main.more.MoreFragment
import com.bookshelfhub.bookshelfhub.ui.main.shelf.ShelfFragment
import com.bookshelfhub.bookshelfhub.ui.main.store.StoreFragment
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

        setUpViewPager()

        layout.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                setViewPagerPosition(newIndex)

            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {}
        })
    }

    private fun setViewPagerPosition(tabIndex:Int){
        when (tabIndex) {
            0 -> {
                layout.viewPager.setCurrentItem(0, false)
            }
            1 -> {
                layout.viewPager.setCurrentItem(1, false)
            }
            2 -> {
                layout.viewPager.setCurrentItem(2, false)
            }
            3 -> {
                layout.viewPager.setCurrentItem(3, false)
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

    private fun setAppThem(isDarkTheme: Boolean) {
        //change activity theme when user switch the theme in more fragment
        val mode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
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
        layout.bottomBar.selectTabAt(layout.viewPager.currentItem, false)
    }


    private fun setUpViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        if(supportFragmentManager.fragments.isEmpty()){
           viewPagerAdapter.addFragment(listOf(
               ShelfFragment.newInstance(),
               StoreFragment.newInstance(),
               BookmarkFragment.newInstance(),
               MoreFragment.newInstance()
           ))
        }

        //Disables Swipe left and right for viewpager
        layout.viewPager.isUserInputEnabled = false
        layout.viewPager.offscreenPageLimit = 4
        layout.viewPager.adapter = viewPagerAdapter
    }


    override fun onDestroy() {
        layout.viewPager.adapter = null
        super.onDestroy()
    }

}