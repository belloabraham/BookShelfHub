package com.bookshelfhub.bookshelfhub.extensions

import android.widget.ImageView
import com.bookshelfhub.bookshelfhub.wrapper.imageloaders.PicassoWrapper

    @JvmSynthetic
     fun ImageView.load(url:String, placeHolder:Int, errorImg:Int=placeHolder, shouldCache:Boolean=true, onComplete:()->Unit={}){
        ImageLoader(this).loadImageWithRoundCorners(url, placeHolder, errorImg, shouldCache, onComplete)
    }

    @JvmSynthetic
    fun ImageView.loadUnCompressed(resId: Int, shouldCache:Boolean=false){
        ImageLoader(this).loadUnCompressed(resId, shouldCache)
    }

    private class ImageLoader(val imageView: ImageView): PicassoWrapper(imageView)
