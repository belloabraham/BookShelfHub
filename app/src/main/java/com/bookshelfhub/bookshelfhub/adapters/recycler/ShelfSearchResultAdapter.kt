package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.ISearchResult
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.uistate.OrderedBookUiState
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


class ShelfSearchResultAdapter(private val context: Context) {

     fun getSearchResultAdapter(): ListAdapter<ISearchResult, RecyclerViewHolder<ISearchResult>> {
        return adapterOf {

            register(
                layoutResource = R.layout.shelf_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model.bookId, model.name, context)
                }
            )

            register(
                layoutResource = R.layout.shelf_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model.bookId, model.name, context)
                }
            )

        }
    }


    companion object{
        fun startBookActivity(isbn:String, title:String, context: Context){
            val intent = Intent(context, BookActivity::class.java)
            with(intent){
                putExtra(Book.NAME, title)
                putExtra(Book.ID, isbn)
                putExtra(Book.IS_SEARCH_ITEM, true)
            }
            context.startActivity(intent)
        }
    }


    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<ShelfSearchHistory>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(isbn:String, name:String, context: Context){
            title.text = name
            itemCardView.setOnClickListener {
                startBookActivity(isbn, name, context)
            }
        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<OrderedBookUiState>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(isbn:String, name:String, context: Context){
            title.text = name
            itemCardView.setOnClickListener {
                startBookActivity(isbn, name, context)
            }
        }
    }
}