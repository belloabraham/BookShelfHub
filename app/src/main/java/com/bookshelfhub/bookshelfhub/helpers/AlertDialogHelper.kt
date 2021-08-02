package com.bookshelfhub.bookshelfhub.helpers

import android.app.Activity
import android.content.DialogInterface
import android.os.Build
import android.text.Html

class AlertDialogHelper private constructor(private val activity: Activity, private val message: String){

    private lateinit var positiveActionText:String
    private var positiveAction: (() -> Unit)? = null

    private lateinit var negativeActionText:String
    private var negativeAction: (() -> Unit)? = null

    private var cancelable:Boolean = false

    companion object{
        fun with(activity:Activity, message: String): AlertDialogHelper {
            return AlertDialogHelper(activity, message)
        }
        fun with(activity:Activity, message: Int): AlertDialogHelper {
            return AlertDialogHelper(activity, activity.getString(message))
        }
    }
    fun setCancelable(value:Boolean): AlertDialogHelper {
        cancelable = value
        return this
    }

    fun setPositiveAction(actionText:Int, action:()->Unit): AlertDialogHelper {
        setPositiveAction(getString(actionText), action)
        return this
    }

    fun setPositiveAction(actionText:String, action:()->Unit): AlertDialogHelper {
        this.positiveActionText = actionText
        this.positiveAction = action
        return this
    }

    fun setNegativeAction(actionText:Int, action:()->Unit): AlertDialogHelper {
        setNegativeAction(getString(actionText), action)
        return this
    }

    fun setNegativeAction(actionText:String, action:()->Unit): AlertDialogHelper {
        this.negativeActionText = actionText
        this.negativeAction = action
        return this
    }


    private fun getString(value:Int): String {
        return activity.getString(value)
    }

    fun build(): Builder {
        return Builder(this, activity)
    }

   class Builder(private val alertDialogHelper: AlertDialogHelper, val activity: Activity){


        fun showDialog(title:Int){
            showDialog(activity.getString(title))
        }

        fun showDialog(title: String){
            val builder = android.app.AlertDialog.Builder(activity)
            builder.setMessage( Html.fromHtml(alertDialogHelper.message))
                .setTitle(title)
                .setCancelable(alertDialogHelper.cancelable)

                   alertDialogHelper.positiveAction?.let {
                       builder.setPositiveButton(alertDialogHelper.positiveActionText, DialogInterface.OnClickListener{
                               _, _ -> it()
                   })
                   }

            alertDialogHelper.negativeAction?.let {
                builder.setNegativeButton(alertDialogHelper.negativeActionText, DialogInterface.OnClickListener{
                        _, _ -> it()
                } )
            }
            builder.create().show()
        }


    }

}