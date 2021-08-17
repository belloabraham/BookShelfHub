package com.bookshelfhub.bookshelfhub.extensions

import android.util.Patterns
import org.apache.commons.text.WordUtils
import java.util.regex.Pattern

@JvmSynthetic
fun String.containsUrl(regex:String):Boolean{
    return (this.contains("facebook.com/",true) || this.contains("instagram.com/",true) || this.contains("twitter.com/",true) || this.contains("bit.ly/",true) || this.contains("amazon.com/",true) || this.contains("tinyurl.com/",true) || this.contains("goodreads.com/",true) || this.contains("books.google.com/",true)|| Pattern.compile(regex).matcher(this).matches())
}

@JvmSynthetic
fun String.capitalize(): String? {
    return WordUtils.capitalize(this)
}

@JvmSynthetic
fun String.isValidEmailAddress():Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

@JvmSynthetic
fun String.isValidPhoneNumber():Boolean{
    return Patterns.PHONE.matcher(this).matches()
}