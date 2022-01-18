package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookItemActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.WebViewActivity
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.StoreSearchHistory
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class StoreSearchResultAdapter (private val context:Context) {

    fun startBookItemActivity(name:String, isbn:String, author:String, context: Context){
        val intent = Intent(context, BookItemActivity::class.java)
        with(intent){
            putExtra(Book.NAME.KEY, name)
            putExtra(Book.ISBN.KEY, isbn)
            putExtra(Book.AUTHOR.KEY, author)
            putExtra(Book.IS_SEARCH_ITEM.KEY, true)
        }
        context.startActivity(intent)
    }

    fun getSearchResultAdapter(): ListAdapter<Any, RecyclerViewHolder<Any>> {
        return adapterOf {

            register(
                layoutResource = R.layout.store_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, this@StoreSearchResultAdapter)
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, this@StoreSearchResultAdapter)
                }
            )

            register(
                layoutResource = R.layout.cant_find_a_book,
                viewHolder = ::FindABookViewHolder,
                onBindViewHolder = { vh, _, model ->
                   vh.bindToView(model, context)
                }
            )
        }
    }

    internal class SearchHistoryViewHolder (view: View): RecyclerViewHolder<StoreSearchHistory>(view) {
       private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:StoreSearchHistory, context: Context, storeSearchResultAdapter: StoreSearchResultAdapter){
            title.text = model.title
            author.text = model.author
            itemCardView.setOnClickListener {
                 storeSearchResultAdapter.startBookItemActivity(model.title, model.isbn, model.author, context)
            }
        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<PublishedBook>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:PublishedBook, context: Context, storeSearchResultAdapter: StoreSearchResultAdapter){
            title.text = model.name
            author.text = model.author
            itemCardView.setOnClickListener {
                storeSearchResultAdapter.startBookItemActivity(model.name, model.isbn, model.author, context)
            }
        }
    }

    private class FindABookViewHolder(view: View) : RecyclerViewHolder<BookRequest>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:BookRequest, context: Context){
            title.text = model.title
            itemCardView.setOnClickListener {
                val intent = Intent(context, WebViewActivity::class.java)
                with(intent){
                    putExtra(WebView.TITLE.KEY, context.getString(R.string.request_a_book))
                    putExtra(WebView.URL.KEY, context.getString(R.string.book_req_url))
                }
                context.startActivity(intent)
            }
        }
    }
}