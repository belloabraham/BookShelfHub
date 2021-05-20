package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

class KeyboardUtil {

    fun hideKeyboard(editText:AppCompatEditText){
        val imm: InputMethodManager =
            (editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
       imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun showKeyboard(editText:AppCompatEditText){
        if (editText.context!=null){
            editText.requestFocus()
            val imm: InputMethodManager =
                (editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

    }
}