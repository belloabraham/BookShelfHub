package com.bookshelfhub.bookshelfhub.Utils

import org.threeten.bp.LocalDateTime

 class LocalDateTimeUtil() {

    companion object{

        fun getDateTimeAsString():String{
            return LocalDateTime.now().toString()
        }

        fun getYear():Int{

            return LocalDateTime.now().year
        }

        fun getDayOfMoth():Int{
            return LocalDateTime.now().dayOfMonth
        }
    }

}