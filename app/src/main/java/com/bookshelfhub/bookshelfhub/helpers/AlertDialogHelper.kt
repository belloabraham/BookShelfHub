package com.bookshelfhub.bookshelfhub.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Spanned
import com.bookshelfhub.bookshelfhub.R


class AlertDialogHelper(private val activity:Activity?, private val positiveAction:()->Unit={}, private val negativeAction:()->Unit={}, private val cancelable:Boolean=false) {


    fun showAlertDialog(title:Int, msg:Int, positiveActionText:Int = R.string.ok, negativeActionText:Int?=null){

        activity?.let {
            showAlertDialog(it.getString(title),
                it.getString(msg),
                it.getString(positiveActionText),
                if(negativeActionText!=null){
                    it.getString(negativeActionText)
                }else{
                    null
                }
            )
        }
    }

    fun showAlertDialog(title:Int, msg:String, positiveActionText:Int = R.string.ok, negativeActionText:Int?=null){
        activity?.let {
            showAlertDialog(it.getString(title),
                msg,
                it.getString(positiveActionText),
                if(negativeActionText!=null){
                    it.getString(negativeActionText)
                }else{
                    null
                }
            )
        }
    }

    fun showAlertDialog(title:String, msg:String, positiveActionText:String, negativeActionText:String?){
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(cancelable)
                .setPositiveButton(positiveActionText, DialogInterface.OnClickListener{
                        dialog, id -> positiveAction()
                });
            negativeActionText?.let{
                builder.setNegativeButton(negativeActionText, DialogInterface.OnClickListener{
                        dialog, id -> negativeAction()
                } )
            }
            builder.create().show()
    }

}