package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookItemActivity
import com.bookshelfhub.bookshelfhub.ContentActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.WebViewActivity
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import java.nio.file.WatchEvent

class StoreSearchResultAdapter (private val context:Context) {

    fun getSearchResultAdapter(): ListAdapter<Any, RecyclerViewHolder<Any>> {
        return adapterOf {

            register(
                layoutResource = R.layout.store_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.author.text = model.title
                    vh.itemCardView.setOnClickListener {
                        startBookItemActivity(model.isbn, model.title, model.author)
                    }
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.author.text = model.title
                    vh.itemCardView.setOnClickListener {
                        startBookItemActivity(model.isbn, model.title, model.author)
                    }
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = ::FindABookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.msg
                    vh.itemCardView.setOnClickListener {
                        val intent = Intent(context, WebViewActivity::class.java)
                        with(intent){
                            putExtra(WebView.TITLE.KEY, context.getString(R.string.request_a_book))
                            putExtra(WebView.URL.KEY, context.getString(R.string.book_req_url))
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }
    }

    private fun startBookItemActivity(isbn:String, title:String, author:String){
        val intent = Intent(context, BookItemActivity::class.java)
        with(intent){
            putExtra(Book.TITLE.KEY, title)
            putExtra(Book.ISBN.KEY, isbn)
            putExtra(Book.AUTHOR.KEY, author)
            putExtra(Book.IS_SEARCH_ITEM.KEY, true)
        }
        context.startActivity(intent)
    }

    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }

    private class FindABookViewHolder(view: View) : RecyclerViewHolder<BookRequest>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
    }
}