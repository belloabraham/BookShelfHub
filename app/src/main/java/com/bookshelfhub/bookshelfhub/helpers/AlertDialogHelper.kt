package com.bookshelfhub.bookshelfhub.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface


class AlertDialogHelper(private val activity:Activity, private val title:String, private val msg:String, private val canceable:Boolean=false) {


    fun showAlertDialog( positiveActionText:String, positiveAction:()->Unit, negativeActionText:String?,  negativeAction:()->Unit={} ){

        activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(canceable)
                .setPositiveButton(positiveActionText, DialogInterface.OnClickListener{
                    dialog, id -> positiveAction()
                })
                .setNegativeButton(negativeActionText, DialogInterface.OnClickListener{
                        dialog, id -> negativeAction()
                } )
            builder.create().show()
        }

    }

}