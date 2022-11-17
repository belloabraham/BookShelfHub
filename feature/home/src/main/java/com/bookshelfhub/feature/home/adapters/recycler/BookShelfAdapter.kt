package com.bookshelfhub.feature.home.adapters.recycler

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
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.core.common.extensions.showToast
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.core.common.helpers.storage.AppExternalStorage
import com.bookshelfhub.core.common.helpers.storage.FileExtension
import com.bookshelfhub.core.common.helpers.utils.IconUtil
import com.bookshelfhub.core.common.helpers.utils.Toast
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.bookshelfhub.feature.home.ui.shelf.ShelfViewModel
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


class BookShelfAdapter(
    private val activity: Activity,
    private val shelfViewModel: ShelfViewModel,
    private val lifecycleOwner: LifecycleOwner,
) {

    fun getOrderedBooksAdapter(): ListAdapter<OrderedBookUiState, RecyclerViewHolder<OrderedBookUiState>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.bookId == new.bookId },
                areItemsTheSame = { old, new -> old.bookId == new.bookId }
            )

            register(
                layoutResource = com.bookshelfhub.feature.home.R.layout.ordered_books_item,
                viewHolder =::OrderedBookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(
                        model,
                        activity,
                        shelfViewModel,
                        lifecycleOwner,
                    )
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View) : RecyclerViewHolder<OrderedBookUiState>(view) {
        private val title: TextView = view.findViewById(com.bookshelfhub.feature.home.R.id.title)
        private val imageView: ImageView = view.findViewById(com.bookshelfhub.feature.home.R.id.itemImageView)
        private val progressBar: ProgressBar = view.findViewById(com.bookshelfhub.feature.home.R.id.progress)
        private val downloadActionIcon: ImageView = view.findViewById(com.bookshelfhub.feature.home.R.id.actionAction)
        private val darkOverLay: View = view.findViewById(com.bookshelfhub.feature.home.R.id.darkOverlay)

        fun bindToView(
            model: OrderedBookUiState,
            activity: Activity,
            shelfViewModel: ShelfViewModel,
            lifecycleOwner: LifecycleOwner,
        ) {

            val bookName = model.name
            title.text = bookName
            imageView.setImageBitmap(IconUtil.getBitmap(model.coverDataUrl))

            val bookId = shelfViewModel.getBookIdFromPossiblyMergedIds(model.bookId)
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

                        shelfViewModel.startBookDownload(workData)

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
                            val message =  String.format(activity.getString(R.string.name_download_complete), bookName)
                            activity.showToast(message)
                            shelfViewModel.deleteDownloadState(downloadBookState)
                        }
                    }
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

    }


}