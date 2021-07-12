package com.bookshelfhub.bookshelfhub.adapters.store

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.wrapper.imageloader.load
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import me.ibrahimyilmaz.kiel.pagingDataAdapterOf

class RecommendBooksAdapter(private val activity: Activity){


    fun getRecommendedPagingAdapter(): PagingDataAdapter<PublishedBooks, RecyclerViewHolder<PublishedBooks>> {
        return pagingDataAdapterOf{

            register(
                layoutResource = R.layout.store_book_item,
                viewHolder = ::RecommendedViewHolder,
                onViewHolderCreated = { vh->
                    //you may handle your on click listener
                    vh.itemView.setOnClickListener {

                    }
                },
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.name
                    vh.imageView.load(model.coverUrl, R.drawable.ic_store_item_place_holder)
                }
            )
        }
    }
    
    private class RecommendedViewHolder(view: View): RecyclerViewHolder<PublishedBooks>(view){
        val title: TextView = view.findViewById(R.id.title)
        val imageView: ImageView = view.findViewById(R.id.itemImageView)
    }
}