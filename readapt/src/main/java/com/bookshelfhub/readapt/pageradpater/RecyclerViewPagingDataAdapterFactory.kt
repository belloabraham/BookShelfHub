package com.bookshelfhub.readapt.pageradpater

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bookshelfhub.readapt.core.AdapterFactory
import com.bookshelfhub.readapt.core.RecyclerViewHolder
import com.bookshelfhub.readapt.core.RecyclerViewHolderManager

class RecyclerViewPagingDataAdapterFactory <T : Any> :
    AdapterFactory<T, PagingDataAdapter<T, RecyclerViewHolder<T>>> {

    override fun create(
        recyclerViewHolderManager: RecyclerViewHolderManager<T, RecyclerViewHolder<T>>,
        itemDiffUtil: DiffUtil.ItemCallback<T>?
    ) = RecyclerViewPagingDataAdapter(recyclerViewHolderManager, itemDiffUtil)
}