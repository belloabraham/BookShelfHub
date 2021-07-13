package com.bookshelfhub.readapt.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.readapt.core.AdapterFactory
import com.bookshelfhub.readapt.core.RecyclerViewHolder
import com.bookshelfhub.readapt.core.RecyclerViewHolderManager


class RecyclerViewAdapterFactory<T : Any> :
    AdapterFactory<T, ListAdapter<T, RecyclerViewHolder<T>>> {

    override fun create(
        recyclerViewHolderManager: RecyclerViewHolderManager<T, RecyclerViewHolder<T>>,
        itemDiffUtil: DiffUtil.ItemCallback<T>?
    ) = RecyclerViewAdapter(recyclerViewHolderManager, itemDiffUtil)
}