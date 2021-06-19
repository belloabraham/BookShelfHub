package com.bookshelfhub.bookshelfhub.helpers

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bookshelfhub.bookshelfhub.R
import com.google.android.material.button.MaterialButton

class MaterialDialogHelper(val lifecycleOwner: LifecycleOwner, val context: Context, val onDismissListener:()->Unit={}) {


    fun showBottomSheet(
        view: View,
        positiveBtnText:Int?,
        negativeBtnText:Int?,
        positiveBtnClickListener:()->Unit={},
       negativeBtnClickListener:()->Unit={}){

        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(null, view, true, true, true)
            negativeButton(negativeBtnText){
                positiveBtnClickListener()
            }
            positiveButton(positiveBtnText){
                negativeBtnClickListener()
            }
            onDismiss {
                onDismissListener()
            }
            lifecycleOwner(lifecycleOwner)
        }
    }

    fun showDialog(view:Int,
                   viewMaxWidth:Int,
                   firstBtnId:Int?,
                   firstBtnClickListener:()->Unit,
                   secondBtnId:Int?,
                   secondBtnClickListener:()->Unit={}){

     val dialog =  MaterialDialog(context).show {
            customView(view, null, noVerticalPadding = true, horizontalPadding = true)
            maxWidth(viewMaxWidth)
            lifecycleOwner(lifecycleOwner)
            onDismiss {
                onDismissListener()
            }
        }


        dialog.onShow { materialDialog->
            firstBtnId?.let {
                val firstBtn = materialDialog.getCustomView().findViewById<MaterialButton>(it)
                firstBtn.setOnClickListener {
                    firstBtnClickListener()
                    dialog.dismiss()
                }
            }
            secondBtnId?.let {
                val secondBtn = materialDialog.getCustomView().findViewById<MaterialButton>(it)
                secondBtn.setOnClickListener {
                    secondBtnClickListener()
                    dialog.dismiss()
                }
            }
        }
    }

}