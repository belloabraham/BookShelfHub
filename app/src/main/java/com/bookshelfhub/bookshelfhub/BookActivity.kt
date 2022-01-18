package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.DisplayUtil
import com.bookshelfhub.bookshelfhub.Utils.ShareUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookBinding
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.Utils.EnableWakeLock
import com.bookshelfhub.bookshelfhub.Utils.settings.Settings
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.Fragment
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.*
import com.bookshelfhub.bookshelfhub.views.Toast
import com.bookshelfhub.pdfviewer.listener.OnPageChangeListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.like.LikeButton
import com.like.OnLikeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
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
  @Inject
  lateinit var dynamicLink: IDynamicLink
  @Inject
  lateinit var connectionUtil: ConnectionUtil
  private var bookShareUrl: Uri? = null
  private var publishedBook:PublishedBook? = null
  private lateinit var userId:String
  private var videoLink: String? = null
  private var bookVideos = listOf<BookVideos>()
  private val hideHandler = Handler()
  @Inject
  lateinit var settingsUtil: SettingsUtil


  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    EnableWakeLock(this, lifecycle)

    userId = userAuth.getUserId()
    title = bookActivityViewModel.getBookName()

    layout = ActivityBookBinding.inflate(layoutInflater)
    setContentView(layout.root)

    var isDarkMode = false

    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> {
        isDarkMode = true
      }
    }

    bookActivityViewModel.getLiveReadHistory().observe(this, Observer { readHistory ->
      if (readHistory.isPresent) {
        showReadProgressDialog(readHistory.get())
      }
    })

    layout.bookmarkBtn.setOnLikeListener(object : OnLikeListener{

      override fun liked(likeButton: LikeButton?) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS)
        bookActivityViewModel.addBookmark(currentPage.toInt())
        showToast(R.string.bookmark_added_msg, Toast.LENGTH_SHORT)

      }

      override fun unLiked(likeButton: LikeButton?) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS)
        bookActivityViewModel.deleteFromBookmark(currentPage.toInt())
        showToast(R.string.removed_bookmark_msg, Toast.LENGTH_SHORT)
      }

    })

    layout.videoListBtn.setOnClickListener {
        if (videoLink!=null){
          val orderedBook = bookActivityViewModel.getAnOrderedBook()
          val intent = Intent(this, WebViewActivity::class.java)
          with(intent){
            putExtra(WebView.TITLE.KEY,orderedBook.title)
            putExtra(WebView.URL.KEY, videoLink)
          }
          startActivity(intent)
        }else{
          showToast(R.string.no_video_msg)
        }
    }

    layout.menuBtn.setOnClickListener {

        val view = View.inflate(this, R.layout.book_menu, null)
        val shareBtn = view.findViewById<MaterialCardView>(R.id.shareBtn)
        val aboutBook = view.findViewById<MaterialCardView>(R.id.aboutBook)

        aboutBook.setOnClickListener {
            startBookInfoActivity(R.id.bookInfoFragment)
        }

        shareBtn.setOnClickListener {
            shareBookLink()
        }

        MaterialBottomSheetDialogBuilder(this,  this)
          .setPositiveAction(R.string.close){}
          .showBottomSheet(view)

    }

    layout.audioBtn.setOnClickListener {

    }

    bookActivityViewModel.getLivePublishedBook().observe(this, Observer { book ->
      book?.let {
        if(book.isPresent){
          val pubBook = book.get()
          publishedBook = pubBook
          dynamicLink.generateShortLinkAsync(
            pubBook.name,
            pubBook.description,
            pubBook.coverUrl,
            userId
          ) {
            bookShareUrl = it
          }
        }
      }
      })

    bookActivityViewModel.getLiveOrderedBook().observe(this, Observer { orderedBook ->
      loadBook(isDarkMode, orderedBook)
    })

    bookActivityViewModel.getLiveListOfBookVideos().observe(this, Observer { bookVideos ->
     this.bookVideos = bookVideos
    })

    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    isFullscreen = true

    // Set up the user interaction to manually show or hide the system UI.
    layout.pdfView.setOnClickListener { toggle() }

    bottomNavigationLayout = layout.fullscreenContentControls
  }

  private  fun loadBook(isDarkMode:Boolean, orderedBook:OrderedBooks){
    val isbn=orderedBook.isbn
    val filePath = "$isbn${File.separator}$isbn.pdf"
    val dirPath = AppExternalStorage.getDocumentFilePath(this, orderedBook.pubId, filePath)
    layout.pdfView.fromAsset(dirPath)
      .nightMode(isDarkMode)
      .password(orderedBook.password!!)
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

          checkIfPageIsBookmarked()
          getPageVideoLink(page)

        }
      })
      .enableAnnotationRendering(true)
      .enableSwipe(true)
      .load()
  }


  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    intent?.let {
      val isbn = it.getStringExtra(Book.ISBN.KEY)
      val name = it.getStringExtra(Book.NAME.KEY)
      bookActivityViewModel.loadLiveOrderedBook(isbn!!, name!!)
    }
  }

  private fun showReadProgressDialog(readHistory: History) {
    lifecycleScope.launch(IO) {
      val noOfDismiss = settingsUtil.getInt(Settings.NO_OF_TIME_DISMISSED.KEY, 0)
      withContext(Main) {
        val view = View.inflate(this@BookActivity, R.layout.continue_reading, null)
        view.findViewById<TextView>(R.id.bookName).text = readHistory.bookName
        view.findViewById<TextView>(R.id.percentageText).text =
          String.format(getString(R.string.percent), readHistory.readPercentage)
        view.findViewById<LinearProgressIndicator>(R.id.progressIndicator).progress =
          readHistory.readPercentage

        MaterialBottomSheetDialogBuilder(this@BookActivity, this@BookActivity)
          .setOnDismissListener {
            //Stop showing users the option to disable progress popup after two times of showing them
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
            layout.pdfView.showPage(readHistory.lastPageNumber)
          }
          .showBottomSheet(view)
      }
    }
  }

  private fun startBookInfoActivity(fragmentID:Int){
    val intent = Intent(this, BookInfoActivity::class.java)
    with(intent){
      putExtra(Book.NAME.KEY,bookActivityViewModel.getBookName())
      putExtra(Fragment.ID.KEY, fragmentID)
      putExtra(Book.ISBN.KEY, bookActivityViewModel.getIsbnNo())
    }
    startActivity(intent)
  }

  private fun getPageVideoLink(pageNumber:Int){
    if(bookVideos.isNotEmpty()){
      val bookVideo = bookVideos.filter{
        it.pageNumber == pageNumber
      }
      if(bookVideo.isNotEmpty()){
        this.videoLink =  bookVideo[0].link
        layout.newVideoDot.visibility = VISIBLE
      }else{
        layout.newVideoDot.visibility = GONE
      }
    }
  }

  private fun checkIfPageIsBookmarked(){
    lifecycleScope.launch(IO){
      val bookmark = bookActivityViewModel.getBookmark(currentPage.toInt())
      withContext(Main){
        layout.bookmarkBtn.isLiked = bookmark.isPresent
      }
    }
  }

  private fun shareBookLink(){
    if (bookShareUrl!=null){
      startActivity(ShareUtil.getShareIntent(bookShareUrl.toString(), publishedBook!!.name))
    }else if (!connectionUtil.isConnected()){
      showToast(R.string.internet_connection_required)
    }else if(publishedBook!=null){
        dynamicLink.generateShortLinkAsync(
          publishedBook!!.name,
          publishedBook!!.description,
          publishedBook!!.coverUrl,
          userId
        ){ uri->
          uri?.let {
            bookShareUrl = uri
            startActivity(ShareUtil.getShareIntent(it.toString(), publishedBook!!.name))
          }
        }
    }else{
      showToast(R.string.unable_to_share_book)
    }

  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
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
   bookActivityViewModel.addReadHistory(currentPage.toInt(), totalPages.toInt())
    super.onDestroy()
  }

}