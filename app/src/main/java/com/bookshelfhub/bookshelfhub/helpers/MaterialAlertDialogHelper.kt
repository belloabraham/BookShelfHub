package com.bookshelfhub.bookshelfhub.helpers

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bookshelfhub.bookshelfhub.view.materialsearch.internal.SearchLayout
import com.google.android.material.button.MaterialButton

 class MaterialAlertDialogHelper(
    private val context: Context
) {

     private var positiveBtnClickListener: (() -> Unit)? =null
     private var negativeBtnClickListener: (() -> Unit)? = null
     private var onDismissListener: (() -> Unit)? = null
     @IdRes private var positiveBtnId:Int?=null
     @IdRes private var negativeBtnId:Int? =null

    fun setPositiveBtnId(id:Int, clickListener:()->Unit): MaterialAlertDialogHelper {
        positiveBtnClickListener = clickListener
        positiveBtnId = id
        return this
    }

    fun setNegativeBtnId(id:Int, clickListener:()->Unit): MaterialAlertDialogHelper {
        negativeBtnClickListener = clickListener
        negativeBtnId = id
        return this
    }

    fun build(lifecycleOwner: LifecycleOwner): Builder {
        return Builder(this, context,lifecycleOwner )
    }

    class  Builder(private val mDialogHelper: MaterialAlertDialogHelper, val context: Context, private val lifecycleOwner: LifecycleOwner){

        fun showDialog(
            viewRes:Int,
            viewMaxWidth:Int?,
            view: View?=null
        ){

            val dialog =  MaterialDialog(context).show {
                customView(viewRes, view, noVerticalPadding = true, horizontalPadding = true)
                lifecycleOwner(lifecycleOwner)
                mDialogHelper.onDismissListener?.let {
                    onDismiss {
                        it()
                    }
                }
            }
            viewMaxWidth?.let {
                dialog.maxWidth(it)
            }

            dialog.onShow { materialDialog->
                mDialogHelper.positiveBtnId?.let {
                    val positiveBtn = materialDialog.getCustomView().findViewById<MaterialButton>(it)
                    positiveBtn.setOnClickListener {
                        mDialogHelper.positiveBtnClickListener!!()
                        dialog.dismiss()
                    }
                }
                mDialogHelper.negativeBtnId?.let {
                    val negativeBtn = materialDialog.getCustomView().findViewById<MaterialButton>(it)
                    negativeBtn.setOnClickListener {
                       mDialogHelper.negativeBtnClickListener!!()
                        dialog.dismiss()
                    }
                }
            }
        }

    }

}