package com.bookshelfhub.bookshelfhub.wrapper.image

import android.widget.ImageView

@JvmSynthetic
fun ImageView.load(url:String, placeHolder:Int, shouldCache:Boolean=false, errorImg:Int=placeHolder){
    ImageLoader(this).loadImageWithRoundCorners(url, placeHolder, errorImg, shouldCache)
}

@JvmSynthetic
fun ImageView.load(resId:Int, placeHolder:Int?=null, shouldCache:Boolean=false, errorImg:Int?=placeHolder){
    ImageLoader(this).loadImageWithRoundCorners(resId, placeHolder, errorImg, shouldCache)
}

@JvmSynthetic
fun ImageView.loadUnCompressed(resId: Int, shouldCache:Boolean=false){
    NonCompressImageLoader(this).loadUnCompressed(resId, shouldCache)
}

private class ImageLoader(val imageView: ImageView): CoilWrapper(imageView)

private class NonCompressImageLoader(imageView: ImageView): PicassoWrapper(imageView)