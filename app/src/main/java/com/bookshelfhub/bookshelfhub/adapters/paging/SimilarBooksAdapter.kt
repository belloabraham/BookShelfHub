package com.bookshelfhub.bookshelfhub.adapters.paging

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.BookItemActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil

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

              imageView.setImageBitmap(IconUtil.getBitmap(model.coverUrl))

            imageView.setOnClickListener {
                    val intent = Intent(activity, BookItemActivity::class.java)
                    with(intent){
                        putExtra(Book.NAME, model.name)
                        putExtra(Book.AUTHOR, model.author)
                        putExtra(Book.ID, model.bookId)
                    }
                    activity.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
            }
        }
    }
}