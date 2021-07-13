package com.bookshelfhub.bookshelfhub.wrapper.imageloaders

import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

open class PicassoWrapper(private val imageView: ImageView) {

    fun loadUnCompressed(resId:Int, shouldCache:Boolean){
      val picasso = Picasso.get()
            .load(resId)
              if (!shouldCache){
                  picasso.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
              }
            picasso.into(imageView)
    }
}