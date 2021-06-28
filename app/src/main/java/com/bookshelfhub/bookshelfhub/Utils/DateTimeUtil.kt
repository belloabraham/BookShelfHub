package com.bookshelfhub.bookshelfhub.Utils

import org.threeten.bp.LocalDateTime

class DateTimeUtil {


    companion object{
        fun getDateTimeAsString():String{
            return LocalDateTime.now().toString()
        }

        fun getYear():Int{
            return LocalDateTime.now().year
        }
    }

}