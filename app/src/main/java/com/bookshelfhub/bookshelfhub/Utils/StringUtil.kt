package com.bookshelfhub.bookshelfhub.Utils

import android.util.Patterns
import org.apache.commons.text.WordUtils

class StringUtil{

        fun isValidEmailAddress(email:String):Boolean{
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPhoneNumber(phone:String):Boolean{
            return Patterns.PHONE.matcher(phone).matches()
        }

        fun capitalize(value:String):String{
          return WordUtils.capitalize(value)
        }

}