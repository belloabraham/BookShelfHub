package com.bookshelfhub.feature.book.item.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.core.common.helpers.utils.IconUtil
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.feature.book.item.BookItemActivity
import com.bookshelfhub.feature.book.item.R

class SimilarBooksAdapter(private val activity: Activity, diffCallBack:DiffUtil.ItemCallback<PublishedBookUiState>): PagingDataAdapter<PublishedBookUiState, SimilarBooksAdapter.ViewHolder>(diffCallBack){

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val model = getItem(position)
        model?.let {
            vh.bindToView(it, activity)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    class ViewHolder(parent: ViewGroup):  RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.similar_book_item, parent, false)
    ){
       private val imageView: ImageView = itemView.findViewById(R.id.itemImageView)

        fun bindToView(model: PublishedBookUiState, activity: Activity){

              imageView.setImageBitmap(IconUtil.getBitmap(model.coverDataUrl))

            imageView.setOnClickListener {
                    val intent = Intent(activity, BookItemActivity::class.java)
                    with(intent){
                        putExtra(com.bookshelfhub.core.data.Book.NAME, model.name)
                        putExtra(com.bookshelfhub.core.data.Book.AUTHOR, model.author)
                        putExtra(com.bookshelfhub.core.data.Book.ID, model.bookId)
                    }
                    activity.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
            }
        }
    }
}