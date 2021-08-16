package com.bookshelfhub.bookshelfhub.Utils

import android.util.Patterns
import java.util.regex.Pattern

class StringUtil{

    fun isValidEmailAddress(email:String):Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhoneNumber(phone:String):Boolean{
        return Patterns.PHONE.matcher(phone).matches()
    }

    fun containsUrl(value:String, regex:String):Boolean{
        return (value.contains("facebook.com/",true) || value.contains("instagram.com/",true) || value.contains("twitter.com/",true) || value.contains("bit.ly/",true) || value.contains("amazon.com/",true) || value.contains("tinyurl.com/",true) || value.contains("goodreads.com/",true) || value.contains("books.google.com/",true)|| Pattern.compile(regex).matcher(value).matches())
    }

}