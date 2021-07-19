package com.bookshelfhub.bookshelfhub.Utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Patterns
import androidx.core.text.HtmlCompat
import org.apache.commons.text.WordUtils

class StringUtil{

        fun isValidEmailAddress(email:String):Boolean{
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPhoneNumber(phone:String):Boolean{
            return Patterns.PHONE.matcher(phone).matches()
        }
}