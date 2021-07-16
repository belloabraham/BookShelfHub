package com.bookshelfhub.bookshelfhub.adapters.store

import androidx.recyclerview.widget.DiffUtil
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks

class DiffUtilItemCallback: DiffUtil.ItemCallback<PublishedBooks>() {

    override fun areItemsTheSame(oldItem: PublishedBooks, newItem: PublishedBooks): Boolean {
         return oldItem.isbn == newItem.isbn
    }

    override fun areContentsTheSame(oldItem: PublishedBooks, newItem: PublishedBooks): Boolean {
       return (oldItem.name == newItem.name) && (oldItem.coverUrl == newItem.coverUrl)
    }
}