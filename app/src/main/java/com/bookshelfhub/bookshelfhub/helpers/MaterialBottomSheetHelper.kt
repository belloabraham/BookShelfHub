package com.bookshelfhub.bookshelfhub.helpers

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner

class MaterialBottomSheetHelper(private val context:Context, private val lifecycleOwner: LifecycleOwner) {

    private var positiveActionText:Int =0
    private var positiveAction: (() -> Unit)? = null

    private var negativeActionText:Int = 0
    private var negativeAction: (() -> Unit)? = null

    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(onDismissListener:()->Unit): MaterialBottomSheetHelper {
        this.onDismissListener = onDismissListener
        return this
    }

    fun setPositiveAction(actionText:Int, action:()->Unit): MaterialBottomSheetHelper {
        this.positiveActionText = actionText
        this.positiveAction = action
        return this
    }

    fun setNegativeAction(actionText:Int, action:()->Unit): MaterialBottomSheetHelper {
        this.negativeActionText = actionText
        this.negativeAction = action
        return this
    }

    fun showBottomSheet(
        view: View?=null,
        @LayoutRes viewRes:Int?=null){

        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(viewRes, view,  true, true, true)

            negativeAction?.let {
                negativeButton(negativeActionText){
                    it()
                }
            }

            positiveAction?.let {
                positiveButton(positiveActionText){
                    it()
                }
            }

            onDismissListener?.let {
                onDismiss {
                    it()
                }
            }

            lifecycleOwner(lifecycleOwner)
        }
    }

}