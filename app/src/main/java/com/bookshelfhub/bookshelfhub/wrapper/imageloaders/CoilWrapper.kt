package com.bookshelfhub.bookshelfhub.wrapper.imageloaders

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation

open class CoilWrapper(private val imageView: ImageView) {

    fun loadImageWithRoundCorners(url:String, placeHolder:Int, errorImg:Int, shouldCache:Boolean, cornerRadius:Int=10){
       imageView.load(url){
           placeholder(placeHolder)
           error(errorImg)
           if (!shouldCache){
               memoryCachePolicy(CachePolicy.DISABLED)
           }
           transformations(RoundedCornersTransformation())
       }
    }

    fun loadImageWithRoundCorners(resId:Int, placeHolder:Int?=null, errorImg:Int?=null, shouldCache:Boolean, cornerRadius:Int=10){
        imageView.load(resId){
            placeHolder?.let {
                placeholder(it)
            }
            errorImg?.let {
                error(errorImg)
            }
            if (!shouldCache){
                memoryCachePolicy(CachePolicy.DISABLED)
            }
            transformations(RoundedCornersTransformation())
        }
    }
}