package com.bookshelfhub.bookshelfhub.extensions

import android.widget.ImageView
import com.bookshelfhub.bookshelfhub.wrappers.imageloaders.PicassoWrapper

    @JvmSynthetic
     fun ImageView.load(url:String, placeHolder:Int, errorImg:Int=placeHolder, shouldCache:Boolean=true, onComplete:()->Unit={}){
        val imageLoader = PicassoWrapper(this)
        imageLoader.load(url, placeHolder, errorImg, shouldCache, onComplete)
    }

    @JvmSynthetic
    fun ImageView.loadUnCompressed(resId: Int, shouldCache:Boolean=false){
        val imageLoader = PicassoWrapper(this)
        imageLoader.loadUnCompressed(resId, shouldCache)
    }
