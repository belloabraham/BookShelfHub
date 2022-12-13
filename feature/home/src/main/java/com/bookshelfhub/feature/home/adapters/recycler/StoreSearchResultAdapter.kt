package com.bookshelfhub.feature.home.adapters.recycler

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.feature.book.item.BookItemActivity
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.feature.webview.WebView
import com.bookshelfhub.core.model.BookRequest
import com.bookshelfhub.core.model.entities.StoreSearchHistory
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.feature.home.R
import com.bookshelfhub.feature.webview.WebViewActivity
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class StoreSearchResultAdapter (private val context:Context) {

    fun startBookItemActivity(name:String, isbn:String, author:String, context: Context){
        val intent = Intent(context, BookItemActivity::class.java)
        with(intent){
            putExtra(Book.NAME, name)
            putExtra(Book.ID, isbn)
            putExtra(Book.AUTHOR, author)
            putExtra(Book.IS_SEARCH_ITEM, true)
        }
        context.startActivity(intent)
    }

    fun getSearchResultAdapter(): ListAdapter<Any, RecyclerViewHolder<Any>> {
        return adapterOf {

            register(
                layoutResource = R.layout.store_history_search_item,
                viewHolder = StoreSearchResultAdapter::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, this@StoreSearchResultAdapter)
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = StoreSearchResultAdapter::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, this@StoreSearchResultAdapter)
                }
            )

            register(
                layoutResource = R.layout.cant_find_a_book,
                viewHolder = StoreSearchResultAdapter::RequestForABookViewHolder,
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
        fun bindToView(model: StoreSearchHistory, context: Context, storeSearchResultAdapter: StoreSearchResultAdapter){
            title.text = model.name
            author.text = model.author
            itemCardView.setOnClickListener {
                 storeSearchResultAdapter.startBookItemActivity(model.name, model.bookId, model.author, context)
            }
        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<PublishedBookUiState>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model: PublishedBookUiState, context: Context, storeSearchResultAdapter: StoreSearchResultAdapter){
            title.text = model.name
            author.text = model.author
            itemCardView.setOnClickListener {
                storeSearchResultAdapter.startBookItemActivity(model.name, model.bookId, model.author, context)
            }
        }
    }

    private class RequestForABookViewHolder(view: View) : RecyclerViewHolder<BookRequest>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model: BookRequest, context: Context){
            title.text = model.title
            itemCardView.setOnClickListener {
                val intent = Intent(context, WebViewActivity::class.java)
                with(intent){
                    putExtra(WebView.TITLE, context.getString(R.string.request_a_book))
                    putExtra(WebView.URL, context.getString(R.string.book_req_url))
                }
                context.startActivity(intent)
            }
        }
    }
}