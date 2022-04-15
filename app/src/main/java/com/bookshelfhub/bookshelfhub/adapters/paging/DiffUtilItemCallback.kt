package com.bookshelfhub.bookshelfhub.adapters.paging

import androidx.recyclerview.widget.DiffUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook

/**
 * Used only for Paging adapter of published books
 */

class DiffUtilItemCallback: DiffUtil.ItemCallback<PublishedBook>() {

    override fun areItemsTheSame(oldItem: PublishedBook, newItem: PublishedBook): Boolean {
         return oldItem.bookId == newItem.bookId
    }

    override fun areContentsTheSame(oldItem: PublishedBook, newItem: PublishedBook): Boolean {
       return (oldItem.name == newItem.name) && (oldItem.coverUrl == newItem.coverUrl)
    }
}