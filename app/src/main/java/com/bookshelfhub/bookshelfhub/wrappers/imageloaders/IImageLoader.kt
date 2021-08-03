package com.bookshelfhub.bookshelfhub.wrappers.imageloaders

interface IImageLoader {
    fun loadUnCompressed(resId: Int, shouldCache: Boolean)
    fun load(
        url: String,
        placeHolder: Int,
        errorImg: Int,
        shouldCache: Boolean,
        onSuccess: () -> Unit
    )
}