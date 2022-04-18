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
import com.bookshelfhub.bookshelfhub.extensions.load

class SimilarBooksAdapter(private val activity: Activity, diffCallBack:DiffUtil.ItemCallback<PublishedBook>): PagingDataAdapter<PublishedBook, SimilarBooksAdapter.ViewHolder>(diffCallBack){

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

        fun bindToView(model: PublishedBook, activity: Activity){
                imageView.load(model.coverUrl, R.drawable.ic_store_item_place_holder)

            imageView.setOnClickListener {
                    val intent = Intent(activity, BookItemActivity::class.java)
                    with(intent){
                        putExtra(Book.NAME.KEY, model.name)
                        putExtra(Book.AUTHOR.KEY, model.author)
                        putExtra(Book.ID.KEY, model.bookId)
                    }
                    val options = ActivityOptions.makeSceneTransitionAnimation(activity, it, activity.getString(R.string.trans_book))
                    activity.startActivity(intent, options.toBundle())
            }
        }
    }
}