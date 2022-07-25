package com.bookshelfhub.bookshelfhub.views

import android.app.Activity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText


/**
 * Used to separate user input by a character example is the case of credit card input
 */
class EditTextCreditCardNumberFormatterWatcher(
    private val maxUserInputLength:Int,
    private val inputChunkLen:Int,
    private val inputChuckDividerChar:String,
    val onTextChanged:(s: String?)->Unit={}): TextWatcher {

    private var currentInput=""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        this.onTextChanged(s.toString().replace(inputChuckDividerChar, ""))
    }

    override fun afterTextChanged(editable: Editable?) {
        val userInput = editable!!.toString()
        val userTypedANewNumber = userInput != currentInput

        if(userTypedANewNumber){
            val userInputWithOnlyDigits = userInput.replace(NON_DIGITS_REGEX, "")
            if(userInputWithOnlyDigits.length <= maxUserInputLength){
                currentInput = userInputWithOnlyDigits.chunked(inputChunkLen).joinToString(inputChuckDividerChar)
                editable.filters = arrayOfNulls<InputFilter>(0)
            }
            editable.replace(0, editable.length, currentInput, 0, currentInput.length)
        }

    }

    companion object{
        private val NON_DIGITS_REGEX = Regex("[^\\d]")
    }

}