package com.bookshelfhub.bookshelfhub.adapters.paging

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.BookItemActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil

class StoreListAdapter(private val activity: Activity, diffCallBack:DiffUtil.ItemCallback<PublishedBookUiState>): PagingDataAdapter<PublishedBookUiState, StoreListAdapter.ViewHolder>(diffCallBack){


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
        LayoutInflater.from(parent.context).inflate(R.layout.store_book_item, parent, false)
    ){
       private val title: TextView = itemView.findViewById(R.id.title)
       private val imageView: ImageView = itemView.findViewById(R.id.itemImageView)

        fun bindToView(model: PublishedBookUiState, activity: Activity){

            imageView.setImageBitmap(IconUtil.getBitmap(model.coverUrl))
            title.background = null
            title.text = model.name

            imageView.setOnClickListener {
                    val intent = Intent(activity, BookItemActivity::class.java)
                    with(intent){
                        putExtra(Book.NAME, model.name)
                        putExtra(Book.AUTHOR, model.author)
                        putExtra(Book.ID, model.bookId)
                    }
                    activity.startActivity(intent)
            }
        }
    }
}