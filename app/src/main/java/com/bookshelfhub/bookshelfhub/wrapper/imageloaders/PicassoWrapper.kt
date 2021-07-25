package com.bookshelfhub.bookshelfhub.wrapper.imageloaders

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.lang.Exception

open class PicassoWrapper(private val imageView: ImageView)  {

     fun loadUnCompressed(resId:Int, shouldCache:Boolean){
      val picasso = Picasso.get()
            .load(resId)
              if (!shouldCache){
                  picasso.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                      .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
              }
            picasso.into(imageView)
    }

     fun loadImageWithRoundCorners(url:String, placeHolder:Int, errorImg:Int, shouldCache:Boolean, onSuccess:()->Unit){

      val callback = object :Callback{
          override fun onSuccess() {
              onSuccess()
          }
          override fun onError(e: Exception?) {
          }
      }
        val picasso = Picasso.get()
            .load(url)
            .error(errorImg)
            .placeholder(placeHolder)
            .noFade()
            .fit()
            .centerCrop()
        if (!shouldCache){
            picasso.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(imageView, callback)
        }else{
            picasso.networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, object :Callback{
                override fun onSuccess() {
                    onSuccess()
                }
                override fun onError(e: Exception?) {
                    Picasso.get()
                        .load(url)
                        .noFade()
                        .error(errorImg)
                        .placeholder(placeHolder)
                        .fit()
                        .centerCrop()
                        .into(imageView, callback)
                }
            })
        }
    }
}