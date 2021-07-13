package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.models.ISearchResult
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import com.bookshelfhub.readapt.core.RecyclerViewHolder
import com.idevellopapps.redapt.adapterOf

class ShelfSearchResultAdapter(private val context: Context) {

     fun getSearchResultAdapter(): ListAdapter<ISearchResult, RecyclerViewHolder<ISearchResult>> {
        return adapterOf {

            register(
                layoutResource = R.layout.shelf_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.itemCardView.setOnClickListener {
                        startBookActivity(model.isbn, model.title)
                    }
                }
            )

            register(
                layoutResource = R.layout.shelf_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.itemCardView.setOnClickListener {
                        startBookActivity(model.isbn, model.title)
                    }
                }
            )

        }
    }

    private fun startBookActivity(isbn:String, title:String){
        val intent = Intent(context, BookActivity::class.java)
        with(intent){
            putExtra(Book.TITLE.KEY, title)
            putExtra(Book.ISBN.KEY, isbn)
            putExtra(Book.IS_SEARCH_ITEM.KEY, true)
        }
        context.startActivity(intent)
    }

    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<ShelfSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<OrderedBooks>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }
}