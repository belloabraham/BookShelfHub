package com.bookshelfhub.bookshelfhub.adapters.search

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
import com.bookshelfhub.bookshelfhub.models.ISearchResult
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class StoreSearchResultAdapter (private val context:Context) {

    fun getSearchResultAdapter(): ListAdapter<Any, RecyclerViewHolder<Any>> {
        return adapterOf {

            register(
                layoutResource = R.layout.store_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context)
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context)
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


    companion object{
         fun startBookItemActivity(name:String, isbn:String, author:String, context: Context){
            val intent = Intent(context, BookItemActivity::class.java)
            with(intent){
                putExtra(Book.TITLE.KEY, name)
                putExtra(Book.ISBN.KEY, isbn)
                putExtra(Book.AUTHOR.KEY, author)
                putExtra(Book.IS_SEARCH_ITEM.KEY, true)
            }
            context.startActivity(intent)
        }
    }


    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<StoreSearchHistory>(view) {
       private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:StoreSearchHistory,context: Context){
            title.text = model.title
            author.text = model.title
            itemCardView.setOnClickListener {
               startBookItemActivity(model.title, model.isbn, model.author, context)
            }
        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<PublishedBooks>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:PublishedBooks, context: Context){
            title.text = model.name
            author.text = model.author
            itemCardView.setOnClickListener {
                startBookItemActivity(model.name, model.isbn, model.author, context)
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