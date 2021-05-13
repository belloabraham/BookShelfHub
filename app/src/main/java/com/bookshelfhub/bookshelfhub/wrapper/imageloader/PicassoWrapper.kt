package com.bookshelfhub.bookshelfhub.wrapper.imageloader

import android.widget.ImageView
import com.squareup.picasso.Picasso

open class PicassoWrapper {

    open fun loadImageIntoView(path: Int, imageView: ImageView){
        Picasso.get().load(path).into(imageView)
    }

}