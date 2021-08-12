package com.bookshelfhub.bookshelfhub.views.toast

import android.app.Activity
import com.bookshelfhub.bookshelfhub.R
import es.dmoral.toasty.Toasty

open class Toasty(val activity: Activity, val backgroundColor:Int ) {

    open fun showToast(msg:Int, toastLength:Int = Toasty.LENGTH_SHORT){
        showToast(activity.getString(msg), toastLength)
    }

    open fun showToast(msg:String, toastLength:Int = Toasty.LENGTH_SHORT){

        Toasty.custom(activity, msg, R.drawable.logo, backgroundColor, toastLength, false,
            true
        ).show()
    }

}