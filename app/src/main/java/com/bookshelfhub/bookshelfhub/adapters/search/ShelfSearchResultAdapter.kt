package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.models.ISearchHistory
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class ShelfSearchResultAdapter(private val context: Context) {

     fun getSearchResultAdapter(): ListAdapter<ISearchHistory, RecyclerViewHolder<ISearchHistory>> {
        return adapterOf {

            register(
                layoutResource = R.layout.shelf_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                }
            )

            register(
                layoutResource = R.layout.shelf_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                }
            )

        }
    }

    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<ShelfSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
    }
}