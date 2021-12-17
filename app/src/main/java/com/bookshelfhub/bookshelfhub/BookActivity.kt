package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitvale.switcher.common.toPx
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.DisplayUtil
import com.bookshelfhub.bookshelfhub.Utils.ShareUtil
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.lifecycle.Display
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.History
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.views.Toast
import com.bookshelfhub.pdfviewer.listener.OnPageChangeListener
import com.like.LikeButton
import com.like.OnLikeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class BookActivity : AppCompatActivity(), LifecycleOwner {

  @Inject
  lateinit var userAuth: IUserAuth
  private val bookActivityViewModel: BookActivityViewModel by viewModels()
  private lateinit var layout: ActivityBookBinding
  private lateinit var bottomNavigationLayout: LinearLayout
  private var currentPage:Double = 0.0
  private var totalPages:Double = 0.0
  private lateinit var title:String
  private lateinit var isbn:String
  @Inject
  lateinit var dynamicLink: IDynamicLink
  @Inject
  lateinit var localDb: ILocalDb
  @Inject
  lateinit var connectionUtil: ConnectionUtil
  private var bookShareUrl: Uri? = null
  private var publishedBook:PublishedBook? = null
  private lateinit var userId:String

  private val hideHandler = Handler()


  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Display(this, lifecycle)

    userId = userAuth.getUserId()

    layout = ActivityBookBinding.inflate(layoutInflater)
    setContentView(layout.root)

    bookActivityViewModel.deleteAllHistory()

     title = intent.getStringExtra(Book.TITLE.KEY)!!
     isbn = intent.getStringExtra(Book.ISBN.KEY)!!
    val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)

    if (isSearchItem){
      bookActivityViewModel.addShelfSearchHistory(ShelfSearchHistory(isbn, title, userId, DateTimeUtil.getDateTimeAsString()))
    }

    var isDarkMode = false

    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> {
        isDarkMode = true
      }
    }

    layout.bookmarkBtn.setOnLikeListener(object : OnLikeListener{

      override fun liked(likeButton: LikeButton?) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS)
        val bookmark = Bookmark(userId, isbn, currentPage.toInt(), title)
        bookActivityViewModel.addBookmark(bookmark)
        showToast(R.string.bookmark_added_msg, Toast.LENGTH_SHORT)

      }

      override fun unLiked(likeButton: LikeButton?) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS)
        bookActivityViewModel.deleteFromBookmark(currentPage.toInt(), isbn)
        showToast(R.string.removed_bookmark_msg, Toast.LENGTH_SHORT)
      }

    })

    layout.videoListBtn.setOnClickListener {

    }

    var isMenuOpened = false
    layout.menuBtn.setOnClickListener {
      isMenuOpened = !isMenuOpened
      if(isMenuOpened){
        val avdMenuToClose = getDrawable(R.drawable.avd_menu_to_close) as AnimatedVectorDrawable
        layout.menuBtn.setImageDrawable(avdMenuToClose)
        avdMenuToClose.start()
      }else{
       val avdCloseToMenu = getDrawable(R.drawable.avd_close_to_menu) as AnimatedVectorDrawable
        layout.menuBtn.setImageDrawable(avdCloseToMenu)
        avdCloseToMenu.start()
      }

    }

    layout.audioBtn.setOnClickListener {

    }


    bookActivityViewModel.getLivePublishedBook().observe(this, Observer { book ->
      book?.let {
        publishedBook = book
        dynamicLink.generateShortLinkAsync(
          book.name,
          book.description,
          book.coverUrl,
          userId
        ) {
          bookShareUrl = it
        }
      }
      })


    bookActivityViewModel.getLiveOrderedBook().observe(this, Observer { orderedBook ->

      layout.pdfView.fromAsset("welcome.pdf")
        .nightMode(isDarkMode)
        //TODO .password(orderedBook.password!!)
        .fitEachPage(true)
        .defaultPage(0)
        .onPageChange(object:OnPageChangeListener{
          override fun onPageChanged(page: Int, pageCount: Int) {
            layout.pageNumberText.text = "${page.plus(1)}"
            layout.pageNumLabel.isVisible = page>0
            layout.progressIndicator.isVisible = page>0
            currentPage = page.toDouble()+1
            totalPages = pageCount.toDouble()
            val progress = ((currentPage/totalPages)*100.0)
            layout.progressIndicator.progress = progress.toInt()

            lifecycleScope.launch(IO){
              val bookmark = localDb.getBookmark(currentPage.toInt(), isbn)
              withContext(Main){
                layout.bookmarkBtn.isLiked = bookmark.isPresent
              }
            }

          }
        })
        .enableAnnotationRendering(true)
        .enableSwipe(true).load()

    })

    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    isFullscreen = true

    // Set up the user interaction to manually show or hide the system UI.
    layout.pdfView.setOnClickListener { toggle() }

    bottomNavigationLayout = layout.fullscreenContentControls

    // Upon interacting with UI controls, delay any scheduled hide()
    // operations to prevent the jarring behavior of controls going away
    // while interacting with the UI.
   /* layout.menuBtn.setOnTouchListener(delayHideTouchListener)
    layout.videoListBtn.setOnTouchListener(delayHideTouchListener)
    layout.audioBtn.setOnTouchListener(delayHideTouchListener)*/
  }


  private fun shareBookLink(){
    if (bookShareUrl!=null){
      ShareUtil(this).shareText(bookShareUrl.toString())
    }else if (!connectionUtil.isConnected()){
      showToast(R.string.internet_connection_required)
    }else{
      publishedBook?.let {
        dynamicLink.generateShortLinkAsync(
          it.name,
          it.description,
          it.coverUrl,
          userId
        ){ uri->
          uri?.let {
            bookShareUrl = uri
            ShareUtil(this).shareText(it.toString())
          }
        }
      }
    }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100)
  }

  private fun setTopMargin(topMargin:Float, view: View){
    val parameter = layout.pageNumLabel.layoutParams as FrameLayout.LayoutParams
    parameter.setMargins(parameter.leftMargin, DisplayUtil.convertDpToPixels(this, topMargin).toInt(), parameter.rightMargin, parameter.bottomMargin)
    view.layoutParams = parameter
  }

  @SuppressLint("InlinedApi")
  private val hidePart2Runnable = Runnable {
    // Delayed removal of status and navigation bar
    layout.pdfView.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LOW_PROFILE or
              View.SYSTEM_UI_FLAG_FULLSCREEN or
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
              View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
              View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
              View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    setTopMargin(0f, layout.pageNumLabel)
  }
  private val showPart2Runnable = Runnable {
    // Delayed display of UI elements
    supportActionBar?.show()
    bottomNavigationLayout.visibility = View.VISIBLE
    setTopMargin(24f, layout.pageNumLabel)
  }
  private var isFullscreen: Boolean = false

  private val hideRunnable = Runnable { hide()
    setTopMargin(0f, layout.pageNumLabel)
  }

  /**
   * Touch listener to use for in-layout UI controls to delay hiding the
   * system UI. This is to prevent the jarring behavior of controls going away
   * while interacting with activity UI.
   */
  private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
    when (motionEvent.action) {
      MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS)
      }
      MotionEvent.ACTION_UP -> view.performClick()
      else -> {
      }
    }
    false
  }

  private fun toggle() {
    if (isFullscreen) {
      hide()
    } else {
      show()
    }
  }

  private fun hide() {
    // Hide UI first
    supportActionBar?.hide()
    bottomNavigationLayout.visibility = View.GONE
    isFullscreen = false

    // Schedule a runnable to remove the status and navigation bar after a delay
    hideHandler.removeCallbacks(showPart2Runnable)
    hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
  }

  private fun show() {
    // Show the system bar
    layout.pdfView.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
              View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    isFullscreen = true

    // Schedule a runnable to display UI elements after a delay
    hideHandler.removeCallbacks(hidePart2Runnable)
    hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
  }

  /**
   * Schedules a call to hide() in [delayMillis], canceling any
   * previously scheduled calls.
   */
  private fun delayedHide(delayMillis: Int) {
    hideHandler.removeCallbacks(hideRunnable)
    hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
  }

  companion object {
    /**
     * Whether or not the system UI should be auto-hidden after
     * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
     */
    private const val AUTO_HIDE = true

    /**
     * If [AUTO_HIDE] is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private const val AUTO_HIDE_DELAY_MILLIS = 3000

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private const val UI_ANIMATION_DELAY = 300
  }

  override fun onDestroy() {

    val percentage = (currentPage/totalPages)*100
    val  readHistory = History(isbn, currentPage.toInt(), percentage.toInt(), title)

    lifecycleScope.launch(IO){
      bookActivityViewModel.addReadHistory(readHistory)
    }

    super.onDestroy()
  }

}