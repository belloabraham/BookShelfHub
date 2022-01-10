package com.bookshelfhub.bookshelfhub.views

import android.app.Activity
import com.bookshelfhub.bookshelfhub.R
import es.dmoral.toasty.Toasty

 class Toast(val activity: Activity, private val backgroundColor:Int = R.color.light_blue_A400 ) {

     fun showToast(msg:Int, toastLength:Int){
        showToast(activity.getString(msg), toastLength)
    }

     fun showToast(msg:String, toastLength:Int){
        Toasty.custom(activity, msg, R.drawable.logo, backgroundColor, toastLength, false,
            true
        ).show()
    }

     companion object{
        const val LENGTH_LONG = Toasty.LENGTH_LONG
        const val LENGTH_SHORT = Toasty.LENGTH_SHORT
     }

}