package com.bookshelfhub.bookshelfhub.view.about


import android.graphics.Bitmap
import android.view.View
import com.bookshelfhub.bookshelfhub.view.ViewIdGenerator


class Item(private var icon: Bitmap?, private var label: String?, private var onClick: View.OnClickListener?) {

    private var id = 0


    fun getLabel(): String? {
        return label
    }

    fun setLabel(label: String?) {
        this.label = label
    }

    fun getIcon(): Bitmap? {
        return icon
    }

    fun setIcon(icon: Bitmap?) {
        this.icon = icon
    }

    fun getOnClick(): View.OnClickListener? {
        return onClick
    }

    fun setOnClick(onClick: View.OnClickListener?) {
        this.onClick = onClick
    }

    fun getId(): Int {
        return id
    }

}