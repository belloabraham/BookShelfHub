package com.bookshelfhub.bookshelfhub.wrapper.imageloader

import android.widget.ImageView

@JvmSynthetic
fun ImageView.load(url:String, placeHolder:Int, shouldCache:Boolean=false, errorImg:Int=placeHolder){
    ImageLoader(this).loadImageWithRoundCorners(url, placeHolder, errorImg, shouldCache)
}

@JvmSynthetic
fun ImageView.load(url:Int, placeHolder:Int?=null, shouldCache:Boolean=false, errorImg:Int?=placeHolder){
    ImageLoader(this).loadImageWithRoundCorners(url, placeHolder, errorImg, shouldCache)
}