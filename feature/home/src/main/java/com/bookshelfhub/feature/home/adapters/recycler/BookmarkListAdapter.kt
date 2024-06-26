package com.bookshelfhub.feature.home.adapters.recycler

import android.app.Activity
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

class BookmarkListAdapter( private val activity: Activity) {

     fun getBookmarkListAdapter(onItemLongClickListener:()->Boolean): ListAdapter<Bookmark, RecyclerViewHolder<Bookmark>> {
        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.id == new.id },
                areItemsTheSame = { old, new -> old.id == new.id }
            )

            register(
                layoutResource = R.layout.bookmarks_page_item,
                viewHolder = BookmarkListAdapter::BookmarkListViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, activity, onItemLongClickListener)
                }
            )

        }
    }


    private class BookmarkListViewHolder (view: View): RecyclerViewHolder<Bookmark>(view) {
        private val label: TextView = view.findViewById(R.id.label)
        private val pageNum: TextView = view.findViewById(R.id.pageNumb)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        private val bookName: TextView = view.findViewById(R.id.bookName)

        fun bindToView(model: Bookmark, activity: Activity, onLongClickListener:()->Boolean) {
            label.text =  model.label
            bookName.text = model.bookName
            pageNum.text =  String.format(activity.getString(R.string.pageNum), model.pageNumb.plus(1))

            itemCardView.setOnClickListener {
                val intent = Intent(activity, BookActivity::class.java)
                with(intent){
                    putExtra(Book.NAME, model.bookName)
                    putExtra(Book.ID, model.bookId)
                    putExtra(Book.BOOKMARK_PAGE_NO, model.pageNumb)
                }
                activity.startActivity(intent)
            }
           itemCardView.setOnLongClickListener {
               onLongClickListener()
           }
        }
    }

}