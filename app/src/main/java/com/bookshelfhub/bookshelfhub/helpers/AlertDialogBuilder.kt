package com.bookshelfhub.bookshelfhub.helpers

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.text.Html

class AlertDialogBuilder private constructor(private val message: String){

    private lateinit var positiveActionText:String
    private var positiveAction: (() -> Unit)? = null

    private lateinit var negativeActionText:String
    private var negativeAction: (() -> Unit)? = null

    private var cancelable:Boolean = false

    companion object{
        fun with(message: String): AlertDialogBuilder {
            return AlertDialogBuilder(message)
        }
    }
    fun setCancelable(value:Boolean): AlertDialogBuilder {
        cancelable = value
        return this
    }

    fun setPositiveAction(actionText:String, action:()->Unit): AlertDialogBuilder {
        this.positiveActionText = actionText
        this.positiveAction = action
        return this
    }


    fun setNegativeAction(actionText:String, action:()->Unit): AlertDialogBuilder {
        this.negativeActionText = actionText
        this.negativeAction = action
        return this
    }

    inner class Builder(val activity: Activity){

        private val alertDialogBuilder = this@AlertDialogBuilder

        fun showDialog(title:Int){
            showDialog(activity.getString(title))
        }

        fun showDialog(title: String){
            val builder = android.app.AlertDialog.Builder(activity)
            builder.setMessage( Html.fromHtml(alertDialogBuilder.message))
                .setTitle(title)
                .setCancelable(alertDialogBuilder.cancelable)

                   alertDialogBuilder.positiveAction?.let {
                       builder.setPositiveButton(alertDialogBuilder.positiveActionText) { _, _ ->
                           it()
                       }
                   }

            alertDialogBuilder.negativeAction?.let {
                builder.setNegativeButton(alertDialogBuilder.negativeActionText) { _, _ ->
                    it()
                }
            }
            builder.create().show()
        }


    }

}