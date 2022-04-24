package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.services.BookDownloadService
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.FirebaseCloudStorage
import com.bookshelfhub.downloadmanager.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class OrderedBooksAdapter(
    private val activity: Activity,
    private val cloudStorage: FirebaseCloudStorage
) {

    fun getOrderedBooksAdapter(): ListAdapter<OrderedBook, RecyclerViewHolder<OrderedBook>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.bookId == new.bookId },
                areItemsTheSame = { old, new -> old.bookId == new.bookId }
            )

            register(
                layoutResource = R.layout.ordered_books_item,
                viewHolder = OrderedBooksAdapter::OrderedBookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, activity, cloudStorage)
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View) : RecyclerViewHolder<OrderedBook>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val imageView: ImageView = view.findViewById(R.id.itemImageView)
        private val progressBar: ProgressBar = view.findViewById(R.id.progress)
        private val actionIcon: ImageView = view.findViewById(R.id.actionAction)
        private val darkOverLay: View = view.findViewById(R.id.darkOverlay)


        fun bindToView(model: OrderedBook, activity: Activity, cloudStorage: FirebaseCloudStorage) {

            title.text = model.name
            imageView.setImageBitmap(IconUtil.getBitmap(model.coverUrl))

            val bookId = getBookId(model.bookId)
            val pubId = model.pubId
            val fileName = "$bookId${FileExtension.DOT_PDF}"
            val fileDoesNotExist = !AppExternalStorage.getDocumentFilePath(
                pubId,
                bookId,
                fileName, activity.applicationContext).exists()

            if (fileDoesNotExist) {
                actionIcon.visibility = VISIBLE
                darkOverLay.visibility = VISIBLE
            }

            imageView.setOnClickListener {
                val fileExist = !fileDoesNotExist
                if (fileExist){
                    openBook(activity, model.name, bookId)
                }

                if(fileDoesNotExist){


                    //Start File download one time uniue expidited work here

                }

            }

        }


        private fun openBook(activity: Activity, bookName: String, bookId:String) {
            val intent = Intent(activity, BookActivity::class.java)
            with(intent) {
                putExtra(Book.NAME, bookName)
                putExtra(Book.ID, bookId)
            }
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                imageView,
                activity.getString(R.string.trans_book)
            )
            activity.startActivity(intent, options.toBundle())
        }

        private fun getBookId(value:String): String {
            return if(value.contains("-")){
                val pubIdAndOrBookId = value.split("-").toTypedArray()
                pubIdAndOrBookId[1]
            }else{
                value
            }
        }
    }


}