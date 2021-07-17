package com.bookshelfhub.bookshelfhub.adapters.store

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.BookItemActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.extensions.load
import kotlinx.coroutines.runBlocking

class StoreListAdapter(private val activity: Activity, diffCallBack:DiffUtil.ItemCallback<PublishedBooks>): PagingDataAdapter<PublishedBooks, StoreListAdapter.ViewHolder>(diffCallBack){


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
       private val itemCardView: CardView = itemView.findViewById(R.id.itemCardView)
       private val imageView: ImageView = itemView.findViewById(R.id.itemImageView)

        fun bindToView(model:PublishedBooks, activity: Activity){
                    imageView.load(model.coverUrl, R.drawable.ic_store_item_place_holder){
                        title.background = null
                        title.text = model.name
                    }

                itemCardView.setOnClickListener {
                    val intent = Intent(activity, BookItemActivity::class.java)
                    val options = ActivityOptions.makeSceneTransitionAnimation(activity, it, activity.getString(R.string.trans_book))
                    activity.startActivity(intent, options.toBundle())
            }
        }
    }
}