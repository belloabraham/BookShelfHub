package com.bookshelfhub.bookshelfhub.adapters.paging

import androidx.recyclerview.widget.DiffUtil
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook

class DiffUtilItemCallback: DiffUtil.ItemCallback<PublishedBook>() {

    override fun areItemsTheSame(oldItem: PublishedBook, newItem: PublishedBook): Boolean {
         return oldItem.isbn == newItem.isbn
    }

    override fun areContentsTheSame(oldItem: PublishedBook, newItem: PublishedBook): Boolean {
       return (oldItem.name == newItem.name) && (oldItem.coverUrl == newItem.coverUrl)
    }
}