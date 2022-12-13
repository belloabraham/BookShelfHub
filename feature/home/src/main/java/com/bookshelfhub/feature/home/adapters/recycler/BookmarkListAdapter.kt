package com.bookshelfhub.feature.home.adapters.recycler

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.feature.home.R
import com.bookshelfhub.core.model.entities.Bookmark
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

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
        fun bindToView(model: Bookmark, context: Context, onLongClickListener:()->Boolean) {
            title.text =  model.title
            pageNum.text =  String.format(context.getString(R.string.pageNum), model.pageNumb)
            itemCardView.setOnClickListener {
                val intent = Intent(context, BookActivity::class.java)
                with(intent){
                    putExtra(Book.NAME, model.title)
                    putExtra(Book.ID, model.bookId)
                }
                context.startActivity(intent)
            }
           itemCardView.setOnLongClickListener {
               onLongClickListener()
           }
        }
    }

}