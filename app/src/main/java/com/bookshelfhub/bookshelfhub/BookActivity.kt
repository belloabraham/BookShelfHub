package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookBinding
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.observers.Display
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BookActivity : AppCompatActivity(), LifecycleOwner {

  @Inject
  lateinit var userAuth: IUserAuth
  private val bookActivityViewModel: BookActivityViewModel by viewModels()
  private lateinit var layout: ActivityBookBinding
  private lateinit var fullscreenContent: LinearLayout
  private lateinit var bottomNavigationLayout: LinearLayout

  private val hideHandler = Handler()

  @SuppressLint("InlinedApi")
  private val hidePart2Runnable = Runnable {
    // Delayed removal of status and navigation bar

    fullscreenContent.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LOW_PROFILE or
              View.SYSTEM_UI_FLAG_FULLSCREEN or
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
              View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
              View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
              View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }
  private val showPart2Runnable = Runnable {
    // Delayed display of UI elements
    supportActionBar?.show()
    bottomNavigationLayout.visibility = View.VISIBLE
  }
  private var isFullscreen: Boolean = false

  private val hideRunnable = Runnable { hide() }

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

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Display(this, lifecycle)

    layout = ActivityBookBinding.inflate(layoutInflater)
    setContentView(layout.root)

    val title = intent.getStringExtra(Book.TITLE.KEY)!!
    val isbn = intent.getStringExtra(Book.ISBN.KEY)!!
    val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)

    if (isSearchItem){
      bookActivityViewModel.addShelfSearchHistory(ShelfSearchHistory(isbn, title, userAuth.getUserId(), DateTimeUtil.getDateTimeAsString()))
    }

    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    isFullscreen = true

    // Set up the user interaction to manually show or hide the system UI.
    fullscreenContent = layout.fullscreenContent
    fullscreenContent.setOnClickListener { toggle() }

    bottomNavigationLayout = layout.fullscreenContentControls

    // Upon interacting with UI controls, delay any scheduled hide()
    // operations to prevent the jarring behavior of controls going away
    // while interacting with the UI.
    layout.dummyButton.setOnTouchListener(delayHideTouchListener)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100)
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
    fullscreenContent.systemUiVisibility =
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

  /*  override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }*/
}