package com.bookshelfhub.bookshelfhub.helpers

import android.view.View
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ActivityContext

class MaterialDialogHelper(val lifecycleOwner: LifecycleOwner) {


    fun showBottomSheet(
        view: View,
        positiveBtnText:String?,
        negativeBtnText:String?,
        positiveBtnClickListener:()->Unit,
       negativeBtnClickListener:()->Unit={}){

    }

}