package com.bookshelfhub.bookshelfhub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.settings.Settings
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.adapters.viewpager.ViewPagerAdapter
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.Storage
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Social
import com.bookshelfhub.bookshelfhub.helpers.google.InAppUpdate
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.services.PrivateKeys
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.ui.main.BookmarkFragment
import com.bookshelfhub.bookshelfhub.ui.main.MoreFragment
import com.bookshelfhub.bookshelfhub.ui.main.ShelfFragment
import com.bookshelfhub.bookshelfhub.ui.main.StoreFragment
import com.bookshelfhub.bookshelfhub.workers.RecommendedBooks
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.downloadmanager.DownloadManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {  //EasyPermissions.PermissionCallbacks

    private lateinit var layout: ActivityMainBinding
    private val mainActivityViewModel:MainActivityViewModel by viewModels()
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var privateKeys: PrivateKeys
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var storage: Storage
    @Inject
    lateinit var dynamicLink: IDynamicLink
    @Inject
    lateinit var connectionUtil: ConnectionUtil
    @Inject
    lateinit var userAuth: IUserAuth
    private lateinit var inAppUpdate:InAppUpdate
    private val IN_APP_UPDATE_ACTIVITY_REQUEST_CODE=700
    private var referrer:String?=null
   // private val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private lateinit var userId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO Load secret keys from realtime firebase
        privateKeys.loadPrivateKeys(lifecycleScope)

        userId=userAuth.getUserId()

        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        //TODO Check if there is an update for this app using In App update
        inAppUpdate =  InAppUpdate(this)
        inAppUpdate.checkForNewAppUpdate{ isImmediateUpdate, appUpdateInfo ->

          //TODO Notify the more fragment update button of new update
          mainActivityViewModel.setIsNewUpdate()

             if (isImmediateUpdate){
                 inAppUpdate.startImmediateUpdate(appUpdateInfo, IN_APP_UPDATE_ACTIVITY_REQUEST_CODE)
             }else{
                 inAppUpdate.startFlexibleUpdate(appUpdateInfo, IN_APP_UPDATE_ACTIVITY_REQUEST_CODE){ installState->
                     if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                         newUpdateInstallUpdateMessage()
                     }
                 }
             }
          }

        //TODO Get Nullable referral userID or PubIdAndISBN
         referrer = mainActivityViewModel.getReferrer()

        openReferrerLinkInStore(referrer)

        openReferrerLinkInStore(referrer)

       /* if(isStoragePermissionRequired()){
            if(Permission.hasPermission(this, storagePermission)){
                openReferrerLinkInStore(referrer)
            }else{
                requestStoragePermission()
            }
        }else{
            openReferrerLinkInStore(referrer)
        }*/

        //***TODO Pre generate dynamic link before user request on app share to decrease share sheet load time
        getBookShareReferralLink(userId){
            it?.let {
                val link = it.toString()
                mainActivityViewModel.setUserReferralLink(link)
                lifecycleScope.launch(IO){
                    settingsUtil.setString(Referrer.REF_LINK.KEY,link)
                }
            }
        }

        mainActivityViewModel.getSelectedIndex().observe(this, Observer {
            //TODO Navigate to a particular tab based on set value
           layout.bottomBar.selectTabAt(it, true)
        })

        mainActivityViewModel.getIsNewMoreTabNotif().observe(this, Observer {
            val moreTabIndex = 3
            val notifNumber = mainActivityViewModel.getTotalMoreTabNotification()

            if (notifNumber>0){
                //TODO there is a notification, set notification bubble for the bottom tab of more with the number of notification
                layout.bottomBar.setBadgeAtTabIndex(moreTabIndex, AnimatedBottomBar.Badge("$notifNumber"))
            }else{
                //TODO there are no notification remove any notification badge if there is one
                layout.bottomBar.clearBadgeAtTabIndex(moreTabIndex)
            }

        })

       /*mainActivityViewModel.getUserRecord().observe(this, Observer { userRecord ->
            if (userRecord.mailOrPhoneVerified){
                mainActivityViewModel.setVerifyPhoneOrEmailNotif(0)
            }else{
                mainActivityViewModel.setVerifyPhoneOrEmailNotif(1)
            }
        })*/



        mainActivityViewModel.getBookInterest().observe(this, Observer { bookInterest ->
            //TODO Check if user already added book interest
            if(bookInterest.isPresent && bookInterest.get().added){

                //TODO remove book interest button notification bubble
                mainActivityViewModel.setBookInterestNotifNo(0)

                //TODO Generate recommended books to be shown in book store
                val recommendedBooksWorker =
                    OneTimeWorkRequestBuilder<RecommendedBooks>()
                        .build()
                WorkManager.getInstance(this).enqueueUniqueWork(Tag.recommendedBooksWorker, ExistingWorkPolicy.REPLACE, recommendedBooksWorker)

            }else{
                mainActivityViewModel.setBookInterestNotifNo(1)
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
                    //TODO if user navigates to bookmark or more fragment
                    layout.shelfStoreViewPager.visibility=View.INVISIBLE
                    layout.cartMoreViewPager.visibility=VISIBLE
                    //TODO set active visible view pager to get the last active view pager in the case of activity recreate when theme changes
                    mainActivityViewModel.setActiveViewPager(1)
                }else{
                    layout.shelfStoreViewPager.visibility=VISIBLE
                    layout.cartMoreViewPager.visibility=View.INVISIBLE
                    mainActivityViewModel.setActiveViewPager(0)
                }

                when(newIndex){
                    0-> {
                        //TODO set active page to get the last active page in the case of activity recreate when theme changes
                        mainActivityViewModel.setActivePage(0)
                        layout.shelfStoreViewPager.setCurrentItem(0, true)
                    }
                    1->{
                        mainActivityViewModel.setActivePage(1)
                        layout.shelfStoreViewPager.setCurrentItem(1, true)
                    }
                    2->{
                        mainActivityViewModel.setActivePage(0)
                        layout.cartMoreViewPager.setCurrentItem(0, true)
                    }
                    3->{
                        mainActivityViewModel.setActivePage(1)
                        layout.cartMoreViewPager.setCurrentItem(1, true)
                    }
                }

            }
            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
            }
        })

        mainActivityViewModel.getIsNightMode().observe(this, Observer { isNightMode ->

            // TODO change activity theme when user switch the theme in more fragment
            val mode = if (isNightMode){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        })

        showReadProgressDialog()
    }

    private fun newUpdateInstallUpdateMessage(){
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

        //TODO Check if user has a completed download to prompt them for install
        inAppUpdate.checkForDownloadedOrDownloadingUpdate(IN_APP_UPDATE_ACTIVITY_REQUEST_CODE){
            newUpdateInstallUpdateMessage()
        }

        //TODO Set active view pager and page based on what was last set by bottom nav on select change  to saved state Instance as a result of activity recreated from theme change, as activity recreated create the effect of when resource get reclaimed by the OS
        if(mainActivityViewModel.getActiveViewPager()==0){
            //TODO Programmatic select tab will only will only switch view pager in onResume and not on create when activity recreated from theme changed
            layout.bottomBar.selectTabAt(if(mainActivityViewModel.getActivePage()==0) 0  else 1, true)
            layout.shelfStoreViewPager.visibility=VISIBLE
            layout.cartMoreViewPager.visibility=View.INVISIBLE
            layout.shelfStoreViewPager.currentItem = mainActivityViewModel.getActivePage()!!
        }else if (mainActivityViewModel.getActiveViewPager()==1) {
            layout.bottomBar.selectTabAt(if(mainActivityViewModel.getActivePage()==0) 2  else 3, true)
            layout.cartMoreViewPager.currentItem = mainActivityViewModel.getActivePage()!!
            layout.shelfStoreViewPager.visibility = View.INVISIBLE
            layout.cartMoreViewPager.visibility = VISIBLE
        }


    }

 /*   override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permission.setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //TODO Don not open referral link in the case of new user sign up until user grants storage permission
        openReferrerLinkInStore(referrer)
    }

    private fun reRequestNeverAskAgainPermission(){
        val  resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 0) {
                    if (!Permission.hasPermission(this, storagePermission)){
                        finish()
                    }
                }
            }

        AlertDialogBuilder.with(this, R.string.storage_perm_disabled_msg)
            .setPositiveAction(R.string.ok){
                resultLauncher.launch(Intent(android.provider.Settings.ACTION_SETTINGS))
            }.build().showDialog(R.string.storage_perm_title)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(Permission.isPermissionPermanentlyDenied(this, perms) && !Permission.hasPermission(this, storagePermission)){
            reRequestNeverAskAgainPermission()
        }else{
            finish()
        }
    }*/

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
                        view.findViewById<TextView>(R.id.bookName).text =  it
                        view.findViewById<TextView>(R.id.percentageText).text =
                            String.format(getString(R.string.percent), lastBookPercentage)
                        view.findViewById<LinearProgressIndicator>(R.id.progressIndicator).progress =
                            lastBookPercentage

                        MaterialBottomSheetDialogBuilder(this@MainActivity, this@MainActivity)
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

    private fun openReferrerLinkInStore(referrer:String?){
        referrer?.let {
            //TODO referrer will not be null if referrer exist
            val ref = it
            //TODO Check if a publisher refer the user
            if (ref.length>userId.length){

                val pubIdAndIsbn = ref.split(Referrer.SEPARATOR.KEY)
                val publisherId = pubIdAndIsbn[0]
                val isbn = pubIdAndIsbn[1]
                val intent = Intent(this, BookItemActivity::class.java)
                intent.putExtra(Referrer.BOOK_REFERRED.KEY,isbn)
                val pubRefRecord = PubReferrers(publisherId, isbn)
                //TODO Add publisher ID that refer the user to the database
                mainActivityViewModel.addPubReferrer(pubRefRecord)
                //TODO opened the book that the publisher refer in Book Store
                startActivity(intent)
            }
        }
    }

    private fun getBookShareReferralLink(userId:String, onComplete:(Uri?)->Unit){
        val title = remoteConfig.getString(Social.TITLE.KEY)
        val description = remoteConfig.getString(Social.DESC.KEY)
        val imageUrl = remoteConfig.getString(Social.IMAGE_URL.KEY)
        dynamicLink.generateShortLinkAsync(title, description, imageUrl, userId){
            onComplete(it)
        }
    }

   /* private fun isStoragePermissionRequired(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    }

    private fun requestStoragePermission(){
                if(Permission.isPermissionPermanentlyDenied(this, listOf(storagePermission))){
                    reRequestNeverAskAgainPermission()
                }else {
                    val rational = getString(R.string.storage_perm_msg)
                    Permission.requestPermission(this, rational, Permission.WRITE_STORAGE_RC, storagePermission)
                }
    }*/


    private fun setUpShelfStoreViewPager(){
        val  fragmentList = listOf(ShelfFragment.newInstance(), StoreFragment.newInstance())
        val shelfStoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        layout.shelfStoreViewPager.isUserInputEnabled = false
        layout.shelfStoreViewPager.adapter = shelfStoreAdapter
    }

    private fun setUpCartMoreViewPager(){
        val  fragmentList = listOf(BookmarkFragment.newInstance(), MoreFragment.newInstance())
        val cartMoreAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        layout.cartMoreViewPager.isUserInputEnabled = false
        layout.cartMoreViewPager.adapter = cartMoreAdapter
    }

    private fun startDownload(){
        DownloadManager.download("","", "123.png")
    }

}