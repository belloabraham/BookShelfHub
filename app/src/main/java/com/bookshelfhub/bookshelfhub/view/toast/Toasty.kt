package com.bookshelfhub.bookshelfhub.view.toast

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.bookshelfhub.bookshelfhub.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import es.dmoral.toasty.Toasty

open class Toasty(val context: Activity, val backgroundColor:Int ) {

    open fun showToast(msg:Int, toastLength:Int = Toasty.LENGTH_SHORT){
        showToast(context.getString(msg), toastLength)
    }

    open fun showToast(msg:String, toastLength:Int = Toasty.LENGTH_SHORT){

        Toasty.custom(context, msg, R.drawable.logo, backgroundColor, toastLength, false,
            true
        ).show()
    }

}