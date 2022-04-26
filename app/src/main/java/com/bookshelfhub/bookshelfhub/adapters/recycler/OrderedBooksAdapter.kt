package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.work.*
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.ui.main.ShelfViewModel
import com.bookshelfhub.bookshelfhub.views.Toast
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.DownloadBook
import com.bookshelfhub.bookshelfhub.workers.Worker
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class OrderedBooksAdapter(
    private val activity: Activity,
    private val worker: Worker,
    private val shelfViewModel: ShelfViewModel,
    private val lifecycleOwner: LifecycleOwner,
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
                    vh.bindToView(
                        model,
                        activity,
                        worker,
                        shelfViewModel,
                        lifecycleOwner,
                    )
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View) : RecyclerViewHolder<OrderedBook>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val imageView: ImageView = view.findViewById(R.id.itemImageView)
        private val progressBar: ProgressBar = view.findViewById(R.id.progress)
        private val downloadActionIcon: ImageView = view.findViewById(R.id.actionAction)
        private val darkOverLay: View = view.findViewById(R.id.darkOverlay)


        fun bindToView(
            model: OrderedBook,
            activity: Activity,
            worker: Worker,
            shelfViewModel: ShelfViewModel,
            lifecycleOwner: LifecycleOwner,
        ) {

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
                setDownloadIconVisibility(VISIBLE)
            }

            imageView.setOnClickListener {
                val fileExist = !fileDoesNotExist
                if (fileExist){
                    openBook(activity, bookName, bookId)
                }

                if(fileDoesNotExist){

                    if(shelfViewModel.isConnected()){
                        val downloadDrawable =  IconUtil.getDrawable(activity, R.drawable.download_outline)
                        downloadActionIcon.setImageDrawable(downloadDrawable)

                        val workData = workDataOf(
                            Book.ID to bookId,
                            Book.SERIAL_NO to model.serialNo.toInt(),
                            Book.PUB_ID to model.pubId,
                            Book.NAME to bookName
                        )

                        lifecycleOwner.lifecycleScope.launch {
                            startDownloadWorker(worker, workData, shelfViewModel)
                        }

                    }else{
                        activity.showToast(R.string.no_internet_error_msg, Toast.LENGTH_LONG)
                    }
                }
            }


            lifecycleOwner.lifecycleScope.launch {
                shelfViewModel.getLiveBookDownloadState(bookId).asFlow().collect{

                    if(it.isPresent){
                        val downloadBookState = it.get()
                        val progress = downloadBookState.progress
                        progressBar.progress = progress

                        if(downloadBookState.hasError){
                            val retryDrawable =  IconUtil.getDrawable(activity, R.drawable.error_reload)
                            downloadActionIcon.setImageDrawable(retryDrawable)
                        }

                        if(progress==100){
                            setDownloadIconVisibility(GONE)
                            val message =  String.format(activity.getString(R.string.download_complete), bookName)
                            activity.showToast(message)
                            shelfViewModel.deleteDownloadState(downloadBookState)
                        }
                    }


                }
            }
        }

        private suspend fun startDownloadWorker(worker:Worker, workData:Data, shelfViewModel:ShelfViewModel){
            val bookId = workData.getString(Book.ID)!!
            val expeditedDownloadBookWorker =
                OneTimeWorkRequestBuilder<DownloadBook>()
                    .setConstraints(Constraint.getConnected())
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(workData)
                    .build()
            worker.enqueueUniqueWork(bookId, ExistingWorkPolicy.KEEP, expeditedDownloadBookWorker)

            worker.getWorkInfoByIdLiveData(expeditedDownloadBookWorker.id).asFlow().collect{
                val workIsEnqueuedButNotStarted = it.state == WorkInfo.State.ENQUEUED
                if(workIsEnqueuedButNotStarted){
                    val initialDownloadProgress = (0..10).random()
                    //To give the user the impression that download
                    // have started so they do not tap on the download button again
                    shelfViewModel.addDownloadState(
                        BookDownloadState(bookId, initialDownloadProgress)
                    )
                }
            }
        }

        private fun setDownloadIconVisibility(visibility:Int){
            downloadActionIcon.visibility = visibility
            darkOverLay.visibility = visibility
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