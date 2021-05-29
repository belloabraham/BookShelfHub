package com.bookshelfhub.bookshelfhub.Utils

import android.util.Patterns

class StringUtils{

    fun isValidEmailAddress(email:String):Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhoneNumber(phone:String):Boolean{
        return Patterns.PHONE.matcher(phone).matches()
    }
}