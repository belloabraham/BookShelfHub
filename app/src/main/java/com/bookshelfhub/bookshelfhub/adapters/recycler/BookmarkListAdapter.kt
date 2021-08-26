package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class BookmarkListAdapter( private val context: Context) {

     fun getBookmarkListAdapter(onItemLongClickListener:()->Boolean): ListAdapter<Bookmark, RecyclerViewHolder<Bookmark>> {
        return adapterOf {
            register(
                layoutResource = R.layout.bookmarks_page_item,
                viewHolder = BookmarkListAdapter::BookmarkListViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, onItemLongClickListener)
                }
            )

        }
    }


    private class BookmarkListViewHolder (view: View): RecyclerViewHolder<Bookmark>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val pageNum: TextView = view.findViewById(R.id.pageNumb)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:Bookmark, context: Context, onLongClickListener:()->Boolean) {
            title.text =  model.title
            pageNum.text =  String.format(context.getString(R.string.pageNum), model.pageNumb)
            itemCardView.setOnClickListener {
            }
           itemCardView.setOnLongClickListener {
               onLongClickListener()
           }
        }
    }

}