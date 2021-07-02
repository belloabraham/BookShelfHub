package com.bookshelfhub.bookshelfhub.adapters.search.viewholder.shelf

import android.view.View
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class SearchHistoryViewHolder (view: View): RecyclerViewHolder<ShelfSearchHistory>(view) {
    val title: TextView = view.findViewById(R.id.title)
}