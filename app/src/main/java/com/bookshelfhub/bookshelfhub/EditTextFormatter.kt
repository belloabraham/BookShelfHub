package com.bookshelfhub.bookshelfhub

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher

class EditTextFormatter(private val userInputLen:Int, private val inputChunkDivider:String, private val inputChunkLen:Int, val onTextChanged:(s: CharSequence?)->Unit={}): TextWatcher {

    private var currentInput=""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged(s)
    }

    override fun afterTextChanged(s: Editable?) {
        val userInput = s!!.toString()
        if (userInput != currentInput && userInput.length<=userInputLen){
            currentInput = userInput.chunked(inputChunkLen).joinToString { inputChunkDivider }
            s.filters = arrayOfNulls<InputFilter>(0)
        }
        s.replace(0, s.length, currentInput, 0, currentInput.length)
    }


}