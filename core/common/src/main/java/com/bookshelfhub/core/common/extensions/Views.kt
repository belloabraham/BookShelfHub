package com.bookshelfhub.core.common.extensions
import android.widget.TextView
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks

fun TextView.applyLinks(links:List<Link>){
    this.applyLinks(links)
}