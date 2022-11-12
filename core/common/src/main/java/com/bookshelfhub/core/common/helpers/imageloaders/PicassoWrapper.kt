package com.bookshelfhub.core.common.helpers.imageloaders

import android.widget.ImageView
import com.squareup.picasso.*
import java.lang.Exception

open class PicassoWrapper(private val imageView: ImageView) : IImageLoader {

    override fun loadUnCompressed(resId:Int, shouldCache:Boolean){
          val picasso = Picasso.get().load(resId)

          if (!shouldCache){
              picasso.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                  .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
          }
         picasso.into(imageView)
    }

    private fun getLoadCallBack(onLoadSuccess:()->Unit, onLoadError:()->Unit,): Callback {
       return object :Callback{
            override fun onSuccess() {
                onLoadSuccess()
            }
            override fun onError(e: Exception?) {
                onLoadError()
            }
        }
    }

     override fun load(url:String, placeHolder:Int, errorImg:Int, shouldCache:Boolean, onSuccess:()->Unit){
            val callback = getLoadCallBack(onSuccess){}
            val picasso = Picasso.get().load(url).error(errorImg).placeholder(placeHolder)
                .fit().centerCrop()
            if (shouldCache){
                loadCached(picasso, url, placeHolder, errorImg, callback)
            }else{
                picasso.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(imageView, callback)
            }
    }

    private fun loadCached(picasso:RequestCreator, url:String, placeHolder:Int, errorImg:Int, callback:Callback){
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

    override fun load(url:String, onError:()->Unit){
        val callback = getLoadCallBack({}, onError)
        Picasso.get()
            .load(url)
            .fit()
            .centerCrop()
            .into(imageView, callback)
    }


}