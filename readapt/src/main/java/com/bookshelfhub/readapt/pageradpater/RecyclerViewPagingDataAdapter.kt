package com.bookshelfhub.readapt.pageradpater

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.bookshelfhub.readapt.core.RecyclerViewHolder
import com.bookshelfhub.readapt.core.RecyclerViewHolderManager
import com.bookshelfhub.readapt.core.RecyclerViewItemCallback

class RecyclerViewPagingDataAdapter <T : Any, VH : RecyclerViewHolder<T>>(
    private val recyclerViewHolderManager: RecyclerViewHolderManager<T, VH>,
    diffUtilItemCallback: ItemCallback<T> = RecyclerViewItemCallback()
) : PagingDataAdapter<T, VH>(diffUtilItemCallback) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH = recyclerViewHolderManager.instantiate(
        parent,
        viewType
    ) as VH

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) = recyclerViewHolderManager.onBindViewHolder(
        holder,
        position,
        checkNotNull(getItem(position))
    )

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: MutableList<Any>
    ) = recyclerViewHolderManager.onBindViewHolder(
        holder,
        position,
        checkNotNull(getItem(position)),
        payloads
    )

    override fun getItemViewType(
        position: Int
    ) = recyclerViewHolderManager.getItemViewType(
        checkNotNull(getItem(position))
    )

    companion object {
        @JvmStatic
        operator fun <T : Any, VH : RecyclerViewHolder<T>> invoke(
            recyclerViewHolderManager: RecyclerViewHolderManager<T, VH>,
            diffUtilCallbackFactory: ItemCallback<T>?
        ) = diffUtilCallbackFactory?.let {
            RecyclerViewPagingDataAdapter(
                recyclerViewHolderManager,
                it
            )
        } ?: RecyclerViewPagingDataAdapter(
            recyclerViewHolderManager
        )
    }
}