package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.models.BookRequest
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
                    vh.title.text = model.title
                    vh.author.text = model.title
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                    vh.author.text = model.title
                }
            )

            register(
                layoutResource = R.layout.store_result_search_item,
                viewHolder = ::FindABookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.msg
                }
            )
        }
    }

    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
    }

    private class FindABookViewHolder(view: View) : RecyclerViewHolder<BookRequest>(view) {
        val title: TextView = view.findViewById(R.id.title)
    }
}