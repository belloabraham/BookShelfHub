package com.bookshelfhub.bookshelfhub.view.toast

import android.content.Context
import com.bookshelfhub.bookshelfhub.R
import es.dmoral.toasty.Toasty

open class ToastyMsg(val context: Context, val backgroundColor:Int ) {

    open fun showToast(msg:Int){
        showToast(context.getString(msg))
    }

    open fun showToast(msg:String){

        Toasty.custom(context, msg, R.drawable.logo, backgroundColor, Toasty.LENGTH_SHORT, false,
            true
        ).show()
    }

}