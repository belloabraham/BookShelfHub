package com.bookshelfhub.bookshelfhub.adapters.search

import android.view.View
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.LocalSearchHistory
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class SearchViewHolder (view: View): RecyclerViewHolder<LocalSearchHistory>(view) {
    val title: TextView = view.findViewById(R.id.title)
}