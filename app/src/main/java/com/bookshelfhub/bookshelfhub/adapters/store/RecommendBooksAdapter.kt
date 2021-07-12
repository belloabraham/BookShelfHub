package com.bookshelfhub.bookshelfhub.adapters.store

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import com.bookshelfhub.bookshelfhub.BookItemActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.wrapper.image.load
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import me.ibrahimyilmaz.kiel.pagingDataAdapterOf

class RecommendBooksAdapter(private val activity: Activity){

    fun getRecommendedPagingAdapter(): PagingDataAdapter<PublishedBooks, RecyclerViewHolder<PublishedBooks>> {
        return pagingDataAdapterOf{

            diff(
                areContentsTheSame = { old, new -> old.isbn == new.isbn  },
                areItemsTheSame = { old, new -> old.isbn == new.isbn }
            )

            register(
                layoutResource = R.layout.store_book_item,
                viewHolder = ::RecommendedViewHolder,
                onBindViewHolder = { vh, _, model ->
                        vh.title.background = null
                        vh.title.text = model.name
                        vh.imageView.load(model.coverUrl, R.drawable.ic_store_item_place_holder)
                        vh.itemCardView.setOnClickListener {
                            val intent = Intent(activity, BookItemActivity::class.java)
                            activity.startActivity(intent)
                        }
                }
            )
        }
    }
    
    private class RecommendedViewHolder(view: View): RecyclerViewHolder<PublishedBooks>(view){
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        val imageView: ImageView = view.findViewById(R.id.itemImageView)
    }
}