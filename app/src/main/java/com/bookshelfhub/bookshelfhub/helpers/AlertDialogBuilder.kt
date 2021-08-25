package com.bookshelfhub.bookshelfhub.helpers

import android.app.Activity
import android.content.DialogInterface
import android.text.Html


class AlertDialogBuilder private constructor(private val activity: Activity, private val message: String){

    private lateinit var positiveActionText:String
    private var positiveAction: (() -> Unit)? = null

    private lateinit var negativeActionText:String
    private var negativeAction: (() -> Unit)? = null

    private var cancelable:Boolean = false

    companion object{
        fun with(activity:Activity, message: String): AlertDialogBuilder {
            return AlertDialogBuilder(activity, message)
        }
        fun with(activity:Activity, message: Int): AlertDialogBuilder {
            return AlertDialogBuilder(activity, activity.getString(message))
        }
    }
    fun setCancelable(value:Boolean): AlertDialogBuilder {
        cancelable = value
        return this
    }

    fun setPositiveAction(actionText:Int, action:()->Unit): AlertDialogBuilder {
        setPositiveAction(getString(actionText), action)
        return this
    }

    fun setPositiveAction(actionText:String, action:()->Unit): AlertDialogBuilder {
        this.positiveActionText = actionText
        this.positiveAction = action
        return this
    }

    fun setNegativeAction(actionText:Int, action:()->Unit): AlertDialogBuilder {
        setNegativeAction(getString(actionText), action)
        return this
    }

    fun setNegativeAction(actionText:String, action:()->Unit): AlertDialogBuilder {
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

   class Builder(private val alertDialogBuilder: AlertDialogBuilder, val activity: Activity){


        fun showDialog(title:Int){
            showDialog(activity.getString(title))
        }

        fun showDialog(title: String){
            val builder = android.app.AlertDialog.Builder(activity)
            builder.setMessage( Html.fromHtml(alertDialogBuilder.message))
                .setTitle(title)
                .setCancelable(alertDialogBuilder.cancelable)

                   alertDialogBuilder.positiveAction?.let {
                       builder.setPositiveButton(alertDialogBuilder.positiveActionText, DialogInterface.OnClickListener{
                               _, _ -> it()
                   })
                   }

            alertDialogBuilder.negativeAction?.let {
                builder.setNegativeButton(alertDialogBuilder.negativeActionText, DialogInterface.OnClickListener{
                        _, _ -> it()
                } )
            }
            builder.create().show()
        }


    }

}