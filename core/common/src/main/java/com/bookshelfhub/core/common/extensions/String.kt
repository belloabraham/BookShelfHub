package com.bookshelfhub.core.common.extensions

import android.util.Patterns
import org.apache.commons.text.StringEscapeUtils
import org.apache.commons.text.WordUtils
import java.util.regex.Pattern

/**
 * Replaces all new line characters namely \n,\t,\r,
 */
@JvmSynthetic
fun String.escapeJSONSpecialChars(): String {
   return StringEscapeUtils.escapeJson(this)
}

/**
 * Replaces all new line characters namely \n,\t,\r with an empty string
 * And escape all other JSON special characters
 */
fun String.purifyJSONString(): String {
    return this.removeAllNLChars().escapeJSONSpecialChars()
}

/**
 * Replaces all new line characters namely \n,\t,\r with an empty string
 */
@JvmSynthetic
fun String.removeAllNLChars(): String {
    return this.replace("\n", "")
        .replace("\t","")
        .replace("\r", "")
}

@JvmSynthetic
fun String.containsUrl(regex:String):Boolean{
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

