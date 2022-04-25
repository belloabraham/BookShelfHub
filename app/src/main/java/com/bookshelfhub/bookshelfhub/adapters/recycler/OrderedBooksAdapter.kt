package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.work.*
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.FirebaseCloudStorage
import com.bookshelfhub.bookshelfhub.ui.main.ShelfViewModel
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.DownloadBook
import com.bookshelfhub.bookshelfhub.workers.UploadUserData
import com.bookshelfhub.bookshelfhub.workers.Worker
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class OrderedBooksAdapter(
    private val activity: Activity,
    private val worker: Worker,
    private val shelfViewModel: ShelfViewModel

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
                    vh.bindToView(model, activity, worker, shelfViewModel)
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


        fun bindToView(model: OrderedBook, activity: Activity, worker: Worker, shelfViewModel: ShelfViewModel) {

            val bookName = model.name
            title.text = bookName
            imageView.setImageBitmap(IconUtil.getBitmap(model.coverUrl))

            val bookId = getBookId(model.bookId)
            val pubId = model.pubId
            val fileNameWithExt = "$bookId${FileExtension.DOT_PDF}"
            val fileDoesNotExist = !AppExternalStorage.getDocumentFilePath(
                pubId,
                bookId,
                fileNameWithExt, activity).exists()

            if (fileDoesNotExist) {
                actionIcon.visibility = VISIBLE
                darkOverLay.visibility = VISIBLE
            }

            imageView.setOnClickListener {
                val fileExist = !fileDoesNotExist
                if (fileExist){
                    openBook(activity, bookName, bookId)
                }

                if(fileDoesNotExist){

                    val workData = workDataOf(
                        Book.ID to bookId,
                        Book.SERIAL_NO to model.serialNo.toInt(),
                        Book.PUB_ID to model.pubId,
                        Book.NAME to bookName
                    )

                    val expeditedDownloadBookWorker =
                        OneTimeWorkRequestBuilder<DownloadBook>()
                            .setConstraints(Constraint.getConnected())
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .setInputData(workData)
                            .build()
                    worker.enqueueUniqueWork(bookId, ExistingWorkPolicy.KEEP, expeditedDownloadBookWorker)

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