package com.bookshelfhub.feature.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bookshelfhub.core.common.helpers.google.InAppUpdate
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.feature.home.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import com.bookshelfhub.feature.book.item.BookItemActivity
import com.bookshelfhub.feature.home.adapters.viewpager.ViewPagerAdapter
import com.bookshelfhub.feature.home.ui.bookmark.BookmarkFragment
import com.bookshelfhub.feature.home.ui.more.MoreFragment
import com.bookshelfhub.feature.home.ui.shelf.ShelfFragment
import com.bookshelfhub.feature.home.ui.store.StoreFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMainBinding
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    private lateinit var inAppUpdate: InAppUpdate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        inAppUpdate = InAppUpdate(this)
        checkForNewAppUpdate(inAppUpdate)


            mainActivityViewModel.getBookIdFromACollaboratorReferrerId()?.let { bookId->
                /*
                Book Id will exist if collaborator Id exist
                Normal user referrer id does not make it here but in onboarding module  an is only considered if user is signing up for the first time
                */
                    openBookInBookStore(bookId)
            }


        mainActivityViewModel.getSelectedIndex().observe(this) {
            layout.bottomBar.selectTabAt(it, true)
        }

        mainActivityViewModel.getIsNewNotificationAtMoreTab().observe(this) {
            val moreTabIndex = 3
            val thereIsNewNotificationAtMoreTab =
                mainActivityViewModel.getTotalMoreTabNotification() > 0
            if (thereIsNewNotificationAtMoreTab) {
                val totalNumbOfNotification = mainActivityViewModel.getTotalMoreTabNotification()
                layout.bottomBar.setBadgeAtTabIndex(
                    moreTabIndex,
                    AnimatedBottomBar.Badge("$totalNumbOfNotification")
                )
            } else {
                layout.bottomBar.clearBadgeAtTabIndex(moreTabIndex)
            }
        }

        mainActivityViewModel.getLiveOptionalBookInterest().observe(this) { bookInterest ->
            mainActivityViewModel.updatedRecommendedBooksNotification(bookInterest)
        }

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
        layout.viewPager.setCurrentItem(tabIndex, false)
    }

    private fun newInAppUpdateDownloadedCompleteMessage() {
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
                        newInAppUpdateDownloadedCompleteMessage()
                    }
                }
            }
        }
    }

    private fun openBookInBookStore(bookId:String){
        val intent = Intent(this, BookItemActivity::class.java)
        intent.putExtra(Book.ID, bookId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        //Had to call this in onResume just in case the first attempt fail from a poor or no network
        mainActivityViewModel.getAndSaveAppShareDynamicLink()

        //Had to call this in on resume to repeatedly check just in case there is a new update
        inAppUpdate.checkForDownloadedOrDownloadingUpdate(InAppUpdate.ACTIVITY_REQUEST_CODE) {
            newInAppUpdateDownloadedCompleteMessage()
        }

        /*
         This code will only be effective on onResume and not onCreate, this has something to do with the
         viewpager
         */
        setActiveViewPagerAndPageAfterMainActivityThemeChange()
    }

    private fun setActiveViewPagerAndPageAfterMainActivityThemeChange() {
        layout.bottomBar.selectTabAt(layout.viewPager.currentItem, false)
    }


    private fun setUpViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        if(supportFragmentManager.fragments.isEmpty()){
            viewPagerAdapter.addFragment(
                arrayListOf(
                    ShelfFragment.newInstance(),
                    StoreFragment.newInstance(),
                    BookmarkFragment.newInstance(),
                    MoreFragment.newInstance()
                )
            )
        }

        //Disables Swipe left and right for viewpager
        layout.viewPager.isUserInputEnabled = false
        //Number of fragment that should be loaded into memory at once
        layout.viewPager.offscreenPageLimit = 4
        layout.viewPager.adapter = viewPagerAdapter
    }


    override fun onDestroy() {
        layout.viewPager.adapter = null
        super.onDestroy()
    }

}