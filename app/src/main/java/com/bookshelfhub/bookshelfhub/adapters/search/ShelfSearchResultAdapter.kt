package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.ContentActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.models.ISearchResult
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchResult
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class ShelfSearchResultAdapter(private val context: Context) {

     fun getSearchResultAdapter(): ListAdapter<ISearchResult, RecyclerViewHolder<ISearchResult>> {
        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.isbn == new.isbn  },
                areItemsTheSame = { old, new -> old.isbn == new.isbn }
            )

            register(
                layoutResource = R.layout.shelf_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.itemCardView.setOnClickListener {
                        startContentActivity(model.isbn, model.title)
                    }
                }
            )

            register(
                layoutResource = R.layout.shelf_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.itemCardView.setOnClickListener {
                        startContentActivity(model.isbn, model.title)
                    }
                }
            )

        }
    }

    private fun startContentActivity(isbn:String, title:String){
        val intent = Intent(context, ContentActivity::class.java)
        with(intent){
            putExtra(Book.TITLE.KEY, title)
            putExtra(Book.ISBN.KEY, isbn)
            putExtra(Book.IS_SEARCH_ITEM.KEY, true)
        }
        context.startActivity(intent)
    }

    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<ShelfSearchResult>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<OrderedBooks>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }
}