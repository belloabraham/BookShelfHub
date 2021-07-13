@file:kotlin.jvm.JvmName("AdaptersKt")
@file:Suppress("UNCHECKED_CAST")

package com.idevellopapps.redapt

import androidx.paging.PagingDataAdapter
import com.bookshelfhub.readapt.adapter.RecyclerViewAdapterFactory
import com.bookshelfhub.readapt.core.AdapterBuilder
import com.bookshelfhub.readapt.core.RecyclerViewHolder
import com.bookshelfhub.readapt.pageradpater.RecyclerViewPagingDataAdapterFactory
import androidx.recyclerview.widget.ListAdapter

inline fun <T : Any> adapterOf(
    adapterBuilder: AdapterBuilder<T>.() -> Unit
): ListAdapter<T, RecyclerViewHolder<T>> =
    AdapterBuilder<T>().apply(adapterBuilder).build(
        RecyclerViewAdapterFactory()
    ) as ListAdapter<T, RecyclerViewHolder<T>>

inline fun <T : Any> pagingDataAdapterOf(
    adapterBuilder: AdapterBuilder<T>.() -> Unit
): PagingDataAdapter<T, RecyclerViewHolder<T>> =
    AdapterBuilder<T>().apply(adapterBuilder).build(
        RecyclerViewPagingDataAdapterFactory()
    ) as PagingDataAdapter<T, RecyclerViewHolder<T>>

