package com.bookshelfhub.bookshelfhub.extensions

import android.widget.ImageView
import com.bookshelfhub.bookshelfhub.helpers.imageloaders.PicassoWrapper

    @JvmSynthetic
     fun ImageView.load(url:String, placeHolder:Int, errorImg:Int=placeHolder, shouldCache:Boolean=true, onSuccess:()->Unit={}){
        val imageLoader = PicassoWrapper(this)
        imageLoader.load(url, placeHolder, errorImg, shouldCache, onSuccess)
    }

        @JvmSynthetic
        fun ImageView.load(url:String, onError:()->Unit={}){
            val imageLoader = PicassoWrapper(this)
            imageLoader.load(url, onError)
        }

    @JvmSynthetic
    fun ImageView.loadUnCompressed(resId: Int, shouldCache:Boolean=false){
        val imageLoader = PicassoWrapper(this)
        imageLoader.loadUnCompressed(resId, shouldCache)
    }
