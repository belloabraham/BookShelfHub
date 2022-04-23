package com.bookshelfhub.bookshelfhub.helpers

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class KeyboardUtil {

    fun hideKeyboard(editText:EditText){
        val imm: InputMethodManager =
            (editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
       imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun showKeyboard(editText:EditText){
        if (editText.context!=null){
            editText.requestFocus()
            val imm: InputMethodManager =
                (editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}