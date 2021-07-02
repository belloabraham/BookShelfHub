package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.view.View
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class StoreSearchResultAdapter (private val context:Context) {



    class SearchHistoryViewHolder (view: View): RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
    }

    class SearchResultViewHolder(view: View) : RecyclerViewHolder<StoreSearchHistory>(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
    }
}