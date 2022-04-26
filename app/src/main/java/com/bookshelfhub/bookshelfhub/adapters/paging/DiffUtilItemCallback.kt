package com.bookshelfhub.bookshelfhub.adapters.paging

import androidx.recyclerview.widget.DiffUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState

/**
 * Used only for Paging adapter of published books
 */

class DiffUtilItemCallback: DiffUtil.ItemCallback<PublishedBookUiState>() {

    override fun areItemsTheSame(
        oldItem: PublishedBookUiState,
        newItem: PublishedBookUiState
    ): Boolean {
         return oldItem.bookId == newItem.bookId
    }

    override fun areContentsTheSame(oldItem: PublishedBookUiState, newItem: PublishedBookUiState): Boolean {
       return (oldItem.name == newItem.name) && (oldItem.coverUrl == newItem.coverUrl)
    }
}