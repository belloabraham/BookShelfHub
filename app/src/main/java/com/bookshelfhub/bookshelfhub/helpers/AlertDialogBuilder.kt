package com.bookshelfhub.bookshelfhub.helpers

import android.app.Activity
import android.content.Context
import android.text.Html

class AlertDialogBuilder private constructor(private val message: String, private val activity: Activity){

    private lateinit var positiveActionText:String
    private var positiveAction: (() -> Unit)? = null

    private lateinit var negativeActionText:String
    private var negativeAction: (() -> Unit)? = null

    private var cancelable:Boolean = false

    companion object{
        fun with(message: String, activity: Activity): AlertDialogBuilder {
            return AlertDialogBuilder(message, activity)
        }
        fun with(message: Int, activity: Activity): AlertDialogBuilder {
            return AlertDialogBuilder(activity.getString(message), activity)
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

    fun setPositiveAction(actionText:Int, action:()->Unit): AlertDialogBuilder {
        setPositiveAction(getString(actionText), action)
        return this
    }

    fun setNegativeAction(actionText:String, action:()->Unit): AlertDialogBuilder {
        this.negativeActionText = actionText
        this.negativeAction = action
        return this
    }

    fun setNegativeAction(actionText:Int, action:()->Unit): AlertDialogBuilder {
        setNegativeAction(getString(actionText), action)
        return this
    }

    fun build(): Builder {
        return Builder(this)
    }

    private fun getString(value:Int): String {
        return activity.getString(value)
    }


     class Builder(private val alertDialogBuilder:AlertDialogBuilder){

        fun showDialog(title:Int){
            showDialog(alertDialogBuilder.getString(title))
        }

        fun showDialog(title: String){
            val builder = android.app.AlertDialog.Builder(alertDialogBuilder.activity)
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