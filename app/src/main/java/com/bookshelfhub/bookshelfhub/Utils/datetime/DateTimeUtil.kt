package com.bookshelfhub.bookshelfhub.Utils.datetime

import org.threeten.bp.LocalDateTime

 object DateTimeUtil {

     fun getDateTimeAsString():String{
         return LocalDateTime.now().toString()
     }

     fun getYear():Int{
         return LocalDateTime.now().year
     }

}