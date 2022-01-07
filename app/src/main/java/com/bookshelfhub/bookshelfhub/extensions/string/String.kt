package com.bookshelfhub.bookshelfhub.extensions

import android.util.Patterns
import org.apache.commons.text.WordUtils
import java.util.regex.Pattern

@JvmSynthetic
fun String.containsUrl(regex:String):Boolean{
    return Pattern.compile(regex).matcher(this).matches()
}
@JvmSynthetic
fun String.isFullName(regex:String):Boolean{
    return Pattern.compile(regex).matcher(this).matches()
}


/**
 * Capitalize the first Letter of every word in a string
 */
@JvmSynthetic
fun String.capitalize(): String? {
    return WordUtils.capitalize(this)
}


@JvmSynthetic
fun String.isValidEmailAddress():Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if string is a type phone number which is to contain optional + at the start
 * and numbers
 */
@JvmSynthetic
fun String.isPhoneNumber():Boolean{
    return Patterns.PHONE.matcher(this).matches()
}

