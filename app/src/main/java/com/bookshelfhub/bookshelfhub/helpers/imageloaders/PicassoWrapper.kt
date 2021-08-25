package com.bookshelfhub.bookshelfhub.helpers.imageloaders

import android.widget.ImageView
import com.squareup.picasso.*
import java.lang.Exception

open class PicassoWrapper(private val imageView: ImageView) : IImageLoader {

     override fun loadUnCompressed(resId:Int, shouldCache:Boolean){
      val picasso = Picasso.get()
            .load(resId)
              if (!shouldCache){
                  picasso.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                      .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
              }
            picasso.into(imageView)
    }

     override fun load(url:String, placeHolder:Int, errorImg:Int, shouldCache:Boolean, onSuccess:()->Unit){
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
                        .error(errorImg)
                        .placeholder(placeHolder)
                        .fit()
                        .centerCrop()
                        .into(imageView, callback)
                }
            })
        }
    }

    override fun load(url:String, onError:()->Unit){

        val callback = object :Callback{
            override fun onSuccess() {
            }
            override fun onError(e: Exception?) {
                onError()
            }
        }
             Picasso.get()
            .load(url)
             .fit()
             .centerCrop()
            .into(imageView, callback)
    }


}