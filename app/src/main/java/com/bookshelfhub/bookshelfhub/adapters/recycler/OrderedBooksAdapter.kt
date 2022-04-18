package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.BookDownloadService
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.request.DownloadRequest
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import java.io.File

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class OrderedBooksAdapter(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner

) {

    fun getOrderedBooksAdapter(): ListAdapter<OrderedBooks, RecyclerViewHolder<OrderedBooks>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.bookId == new.bookId },
                areItemsTheSame = { old, new -> old.bookId == new.bookId }
            )

            register(
                layoutResource = R.layout.ordered_books_item,
                viewHolder = OrderedBooksAdapter::OrderedBookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, activity, lifecycleOwner)
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View) : RecyclerViewHolder<OrderedBooks>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val imageView: ImageView = view.findViewById(R.id.itemImageView)
        private val progressBar: ProgressBar = view.findViewById(R.id.progress)
        private val actionIcon: ImageView = view.findViewById(R.id.actionAction)
        private val darkOverLay: View = view.findViewById(R.id.darkOverlay)


        private var downloadRequest: DownloadRequest? = null

        fun bindToView(model: OrderedBooks, activity: Activity, lifecycleOwner: LifecycleOwner) {

            val url = model.downloadUrl
            title.text = model.name
            imageView.load(model.coverUrl, R.drawable.ic_store_item_place_holder)
            val isbn = model.bookId
            val bookName = model.name
            val pubId = model.pubId
            val fileName = "$isbn.pdf"
            val dirPath = "$pubId${File.separator}$isbn"
            val isFileExist = isFileExist(activity, dirPath, fileName)

            //If file does not exist, user is yet to download book
            if (!isFileExist) {
                //Show all download controls
                actionIcon.visibility = VISIBLE
                darkOverLay.visibility = VISIBLE
            }

            imageView.setOnClickListener {
                val downloadId = downloadRequest?.getDownloadId()
                val downloadStatus = getDownloadStatus(downloadId)
                when {
                    isFileExist(activity, dirPath, fileName) -> {
                        openBook(activity, model)
                    }

                    downloadStatus == Status.PAUSED -> {
                        resumeBookDownloading(activity, bookName, downloadId)
                    }
                    downloadStatus == Status.RUNNING || downloadStatus == Status.QUEUED -> {
                        pauseBookDownloading(activity, bookName, downloadId)
                    }
                    else -> {
                        startBookDownloading(activity, url, dirPath, fileName, bookName, model.bookId)
                    }
                }
            }


            BookDownloadService.getLiveDownloadRequest().observe(lifecycleOwner, Observer {
                if (it.getUrl() == url && downloadRequest == null) {
                    downloadRequest = it

                    downloadRequest?.setOnPauseListener(object : OnPauseListener {
                        override fun onPause() {
                            //Show Play Icon
                           val pauseDrawable =  IconUtil.getDrawable(activity, R.drawable.reload)
                            actionIcon.setImageDrawable(pauseDrawable)

                        }

                    })?.setOnProgressListener(object : OnProgressListener {
                        override fun onProgress(progress: Progress?) {
                            //Show progress on control
                            progress?.let { mProgress->
                                progressBar.visibility = VISIBLE
                                if(mProgress.currentBytes>0){
                                    val percentage = (mProgress.currentBytes/mProgress.totalBytes)*100
                                    progressBar.progress = percentage.toInt()
                                }
                            }
                        }

                    })?.setOnStartOrResumeListener(object : OnStartOrResumeListener {
                        override fun onStartOrResume() {
                            //Show pause Icon
                            val pauseDrawable =  IconUtil.getDrawable(activity, R.drawable.pause)
                            actionIcon.setImageDrawable(pauseDrawable)
                        }

                    })

                }
            })

            BookDownloadService.getLiveDownloadResult().observe(lifecycleOwner, Observer {
                if (it.id == downloadRequest?.getDownloadId()) {

                    it.error?.let {
                        //Show error controls
                        val pauseDrawable =  IconUtil.getDrawable(activity, R.drawable.error_reload)
                        actionIcon.setImageDrawable(pauseDrawable)
                    }

                    if (it.isComplete && isFileExist(activity, dirPath, fileName)) {
                        //Hide all download controls
                        progressBar.visibility = GONE
                        actionIcon.visibility = GONE
                        darkOverLay.visibility = GONE
                    }
                }
            })
        }


        private fun getDownloadStatus(downloadId: Int?): Status? {
            if (downloadId == null) {
                return null
            }
            return DownloadManager.getStatus(downloadId)
        }

        private fun pauseBookDownloading(context: Context, bookName:String, downloadId: Int?) {
            val intent = Intent(context, BookDownloadService::class.java)
            intent.action = Download.ACTION_PAUSE
            intent.putExtra(Download.DOWNLOAD_ID, downloadId)
            intent.putExtra(Download.BOOK_NAME, bookName)
            context.startService(intent)
        }

        private fun isFileExist(context: Context, dirPath: String, fileName: String): Boolean {
            return AppExternalStorage.isDocumentFileExist(context, dirPath, fileName)
        }

        private fun resumeBookDownloading(context: Context, bookName:String, downloadId: Int?) {
            val intent = Intent(context, BookDownloadService::class.java)
            intent.action = Download.ACTION_RESUME
            intent.putExtra(Download.DOWNLOAD_ID, downloadId)
            intent.putExtra(Download.BOOK_NAME, bookName)
            context.startService(intent)
        }

        private fun startBookDownloading(
            context: Context,
            url: String?,
            dirPath: String,
            fileName: String,
            bookName:String,
            isbn:String
        ) {
            val intent = Intent(context, BookDownloadService::class.java)
            intent.action = Download.ACTION_START
            intent.putExtra(Download.URL, url)
            intent.putExtra(Download.FILE_NAME, fileName)
            intent.putExtra(Download.DIR_PATH, dirPath)
            intent.putExtra(Download.BOOK_NAME, bookName)
            intent.putExtra(Book.ID.KEY, isbn)
            context.startService(intent)
        }

        private fun openBook(activity: Activity, model: OrderedBooks) {
            val intent = Intent(activity, BookActivity::class.java)
            with(intent) {
                putExtra(Book.NAME.KEY, model.name)
                putExtra(Book.ID.KEY, model.bookId)
            }
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                imageView,
                activity.getString(R.string.trans_book)
            )
            activity.startActivity(intent, options.toBundle())
        }
    }

}