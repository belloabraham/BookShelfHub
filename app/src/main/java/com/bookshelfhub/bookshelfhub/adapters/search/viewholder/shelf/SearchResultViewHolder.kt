package com.bookshelfhub.bookshelfhub.adapters.search.viewholder.shelf

import android.view.View
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class SearchResultViewHolder(view: View) : RecyclerViewHolder<StoreSearchHistory>(view) {
    val title: TextView = view.findViewById(R.id.title)
}