package com.bookshelfhub.bookshelfhub.adapters.search.local

import android.view.View
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.CloudSearchHistory
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class SearchResultViewHolder(view: View) : RecyclerViewHolder<CloudSearchHistory>(view) {
    val title: TextView = view.findViewById(R.id.title)
}