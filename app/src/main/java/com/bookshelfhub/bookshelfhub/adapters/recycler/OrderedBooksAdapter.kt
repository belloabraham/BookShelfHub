package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.BookDownloadService
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.OrderedBooks
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
    private val lifecycleOwner: LifecycleOwner) {

    fun getOrderedBooksAdapter(): ListAdapter<OrderedBooks, RecyclerViewHolder<OrderedBooks>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.isbn == new.isbn },
                areItemsTheSame = { old, new -> old.isbn == new.isbn }
            )

            register(
                layoutResource = R.layout.ordered_books_item,
                viewHolder = OrderedBooksAdapter::OrderedBookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, activity, lifecycleOwner, activity)
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View) : RecyclerViewHolder<OrderedBooks>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val imageView: ImageView = view.findViewById(R.id.itemImageView)
        private var downloadRequest: DownloadRequest? = null

        fun bindToView(model: OrderedBooks, context: Context, lifecycleOwner: LifecycleOwner, activity: Activity) {

            val url = model.downloadUrl!!
            title.text = model.title
            imageView.load(model.coverUrl, R.drawable.ic_store_item_place_holder)
            val isbn = model.isbn
            val pubId = model.pubId
            val fileName = "$isbn.pdf"
            val dirPath = "$pubId${File.separator}$isbn"
            val isFileExist = isFileExist(context, dirPath, fileName)

            //If file does not exist, user is yet to download book
            if (!isFileExist){
                //Show all download controls
            }

            imageView.setOnClickListener {
                val downloadId = downloadRequest?.getDownloadId()
                val downloadStatus = getDownloadStatus(downloadId)
                when {
                    isFileExist(context, dirPath, fileName) -> {
                        openBook(activity, model)
                    }
                    downloadStatus == Status.PAUSED -> {
                        resumeBookDownloading(context, url)
                    }
                    downloadStatus == Status.RUNNING || downloadStatus == Status.QUEUED -> {
                        pauseBookDownloading(context, url)
                    }
                    else -> {
                        startBookDownloading(context, url, dirPath, fileName)
                    }
                }
            }


            BookDownloadService.getLiveDownloadRequest().observe(lifecycleOwner, Observer {
                if (it.getUrl() == url && downloadRequest == null) {
                    downloadRequest = it

                    downloadRequest?.setOnPauseListener(object : OnPauseListener {
                        override fun onPause() {
                            //Show Play Icon
                        }

                    })?.setOnProgressListener(object : OnProgressListener {
                        override fun onProgress(progress: Progress?) {
                            //Show progress on control
                        }

                    })?.setOnStartOrResumeListener(object : OnStartOrResumeListener {
                        override fun onStartOrResume() {
                            //Show Pause Icon
                        }

                    })

                }
            })

            BookDownloadService.getLiveDownloadResult().observe(lifecycleOwner, Observer {
                if(it.id==downloadRequest?.getDownloadId()){

                    it.error?.let {
                        //Show error controls
                    }

                    if(it.isComplete && isFileExist(context, dirPath, fileName)){
                        //Hide all download controls
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

        private fun pauseBookDownloading(context: Context, url: String) {
            val intent = Intent(context, BookDownloadService::class.java)
            intent.action = Download.ACTION_PAUSE
            intent.putExtra(Download.URL, url)
            context.startService(intent)
        }

        private fun isFileExist(context:Context, dirPath:String, fileName:String): Boolean {
            return AppExternalStorage.isDocumentFileExist(context, dirPath, fileName)
        }

        private fun resumeBookDownloading(context: Context, url: String) {
            val intent = Intent(context, BookDownloadService::class.java)
            intent.action = Download.ACTION_RESUME
            intent.putExtra(Download.URL, url)
            context.startService(intent)
        }

        private fun startBookDownloading(
            context: Context,
            url: String,
            dirPath: String,
            fileName: String
        ) {
            val intent = Intent(context, BookDownloadService::class.java)
            intent.action = Download.ACTION_START
            intent.putExtra(Download.URL, url)
            intent.putExtra(Download.FILE_NAME, fileName)
            intent.putExtra(Download.DIR_PATH, dirPath)
            context.startService(intent)
        }

        private fun openBook(activity: Activity, model: OrderedBooks) {
            val intent = Intent(activity, BookActivity::class.java)
            with(intent) {
                putExtra(Book.TITLE.KEY, model.title)
                putExtra(Book.ISBN.KEY, model.isbn)
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