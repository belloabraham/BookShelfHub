package com.bookshelfhub.bookshelfhub.extensions

import android.widget.ImageView
import com.bookshelfhub.bookshelfhub.wrapper.imageloaders.CoilWrapper
import com.bookshelfhub.bookshelfhub.wrapper.imageloaders.PicassoWrapper

@JvmSynthetic
fun ImageView.load(url:String, placeHolder:Int, shouldCache:Boolean=false, errorImg:Int=placeHolder){
    ImageLoader(this).loadImageWithRoundCorners(url, placeHolder, errorImg, shouldCache)
}

@JvmSynthetic
fun ImageView.loadUnCompressed(resId: Int, shouldCache:Boolean=false){
    NonCompressImageLoader(this).loadUnCompressed(resId, shouldCache)
}

private class ImageLoader(val imageView: ImageView): CoilWrapper(imageView)

private class NonCompressImageLoader(imageView: ImageView): PicassoWrapper(imageView)