package com.bookshelfhub.book.page


import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.book.page.databinding.ActivityBookBinding
import com.bookshelfhub.core.common.extensions.isAppUsingDarkTheme
import com.bookshelfhub.core.common.extensions.makeUrlPath
import com.bookshelfhub.core.common.extensions.showToast
import com.bookshelfhub.core.common.helpers.EnableWakeLock
import com.bookshelfhub.core.common.helpers.dialog.AlertDialogBuilder
import com.bookshelfhub.core.common.helpers.dialog.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.core.common.helpers.utils.DisplayUtil
import com.bookshelfhub.core.common.helpers.utils.ShareUtil
import com.bookshelfhub.core.common.helpers.utils.Toast
import com.bookshelfhub.core.data.Fragment
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.domain.usecases.LocalFile
import com.bookshelfhub.core.model.entities.OrderedBook
import com.bookshelfhub.core.model.entities.PublishedBook
import com.bookshelfhub.core.model.entities.ReadHistory
import com.bookshelfhub.core.remote.remote_config.RemoteConfig
import com.bookshelfhub.feature.about.book.BookInfoActivity
import com.bookshelfhub.feature.webview.WebView
import com.bookshelfhub.feature.webview.WebViewActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.like.LikeButton
import com.like.OnLikeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
@AndroidEntryPoint
class BookActivity : AppCompatActivity(), LifecycleOwner {

    private val bookActivityViewModel by viewModels<BookActivityViewModel>()
    private lateinit var layout: ActivityBookBinding
    private lateinit var bottomNavigationLayout: LinearLayout
    private var currentPage:Double = 0.0
    private var totalPages:Double = 0.0
    private var publishedBook: PublishedBook? = null
    private val hideHandler = Handler()
    private lateinit var orderedBook:OrderedBook


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EnableWakeLock(this, lifecycle)

        title = bookActivityViewModel.getBookName()

        layout = ActivityBookBinding.inflate(layoutInflater)
        setContentView(layout.root)

        val isDarkMode = isAppUsingDarkTheme()


        lifecycleScope.launch {
             orderedBook = bookActivityViewModel.getAnOrderedBook()
             loadBook(isDarkMode, orderedBook.pubId)

            if(bookActivityViewModel.getBoolean(Settings.SHOW_CONTINUE_POPUP, true)){
               val readHistory = bookActivityViewModel.getReadHistory()
                if (readHistory.isPresent) {
                    showReadProgressDialog(readHistory.get())
                }
            }
        }

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

        layout.bookCategoryVideoLinkBtn.setOnClickListener {
            lifecycleScope.launch {
                val publishedBook = bookActivityViewModel.getPublishedBook().get()
                val videosLink = bookActivityViewModel.getRemoteString(RemoteConfig.VIDEOS_DOMAIN) +"/"+ publishedBook.category.makeUrlPath()
                val intent = Intent(this@BookActivity, WebViewActivity::class.java)
                with(intent){
                    putExtra(WebView.TITLE, publishedBook.category)
                    putExtra(WebView.URL, videosLink)
                }
                startActivity(intent)
            }
        }


        layout.menuBtn.setOnClickListener {
            val view = View.inflate(this, R.layout.book_menu, null)
            val shareBtn = view.findViewById<MaterialCardView>(R.id.shareBtn)
            val aboutBook = view.findViewById<MaterialCardView>(R.id.aboutBook)

            aboutBook.setOnClickListener {
                startBookInfoActivity()
            }

            shareBtn.setOnClickListener {
                shareBookLink()
            }

            MaterialBottomSheetDialogBuilder(this,  this)
                .setPositiveAction(R.string.close){}
                .showBottomSheet(view)

        }

        layout.audioBtn.setOnClickListener {
                AlertDialogBuilder.with(com.bookshelfhub.core.resources.R.string.audio_coming_msg, this)
                    .setCancelable(true)
                    .setPositiveAction(R.string.ok){}
                    .build()
                    .showDialog(com.bookshelfhub.core.resources.R.string.coming_soon)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true
        // Set up the user interaction to manually show or hide the system UI.
        layout.pdfView.setOnClickListener { toggle() }

        bottomNavigationLayout = layout.fullscreenContentControls
    }

    private  fun loadBook(isDarkMode:Boolean, pubId:String){
        val bookFile = LocalFile.getBookFile(bookActivityViewModel.getBookId(), pubId, this)

        layout.pdfView.fromFile(bookFile)
            .nightMode(isDarkMode)
            .fitEachPage(true)
            .defaultPage(0)
            .onPageChange { page, pageCount ->
                layout.pageNumberText.text = "${page.plus(1)}"
                layout.pageNumLabel.isVisible = page > 0
                layout.progressIndicator.isVisible = page > 0
                currentPage = page.toDouble() + 1
                totalPages = pageCount.toDouble()
                val progress = ((currentPage / totalPages) * 100.0)
                layout.progressIndicator.progress = progress.toInt()

                checkIfPageIsBookmarked()
            }
            .enableAnnotationRendering(true)
            .enableSwipe(true)
            .load()
    }

    override fun onResume() {
        super.onResume()
        bookActivityViewModel.generateBookShareLink()
    }


    private fun showReadProgressDialog(readHistory: ReadHistory) {
        lifecycleScope.launch {
            val noOfDismiss = bookActivityViewModel.getInt(Settings.NO_OF_TIME_DISMISSED, 0)
            val view = View.inflate(this@BookActivity, R.layout.continue_reading, null)
            view.findViewById<TextView>(R.id.bookName).text = readHistory.bookName
            view.findViewById<TextView>(R.id.percentageText).text =
                String.format(getString(R.string.percent), readHistory.readPercentage)
            view.findViewById<LinearProgressIndicator>(R.id.progressIndicator).progress =
                readHistory.readPercentage

            MaterialBottomSheetDialogBuilder(this@BookActivity, this@BookActivity)
                .setOnDismissListener {
                    if (noOfDismiss < 2) {
                        showToast(R.string.dismiss_msg)
                        bookActivityViewModel.addIntToSettings(Settings.NO_OF_TIME_DISMISSED,
                            noOfDismiss + 1)
                    }
                }
                .setPositiveAction(R.string.dismiss) {}
                .setNegativeAction(R.string.continue_reading) {
                    layout.pdfView.jumpTo(readHistory.lastPageNumber, true)
                }
                .showBottomSheet(view)
        }
    }

    private fun startBookInfoActivity(){
        val intent = Intent(this, BookInfoActivity::class.java)
        with(intent){
            putExtra(com.bookshelfhub.core.data.Book.NAME, bookActivityViewModel.getBookName())
            putExtra(com.bookshelfhub.core.data.Book.ID, bookActivityViewModel.getBookId())
        }
        startActivity(intent)
    }

    private fun checkIfPageIsBookmarked(){
        lifecycleScope.launch{
            val bookmark = bookActivityViewModel.getBookmark(currentPage.toInt())
            layout.bookmarkBtn.isLiked = bookmark.isPresent
        }
    }

    private fun shareBookLink(){
        val bookShareLink = bookActivityViewModel.getBookShareLink()
        bookShareLink?.let {
            startActivity(ShareUtil.getShareIntent(it, publishedBook!!.name))
        }
        if(bookShareLink==null){
            showToast(R.string.internet_connection_required)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun setTopMargin(topMargin:Float, view: View){
        val parameter = layout.pageNumLabel.layoutParams as FrameLayout.LayoutParams
        parameter.setMargins(
            parameter.leftMargin,
            DisplayUtil.convertDpToPixels(this, topMargin).toInt(),
            parameter.rightMargin, parameter.bottomMargin
        )
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
        bottomNavigationLayout.visibility = VISIBLE
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
        bottomNavigationLayout.visibility = GONE
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
        bookActivityViewModel.addReadHistory(
            currentPage.toInt(),
            totalPages.toInt()
        )
        super.onDestroy()
    }

}