package com.bookshelfhub.bookshelfhub.helpers.utils.datetime

import org.threeten.bp.LocalDateTime

 object DateTimeUtil {

     fun getDateTimeAsString():String{
         return LocalDateTime.now().toString()
     }

     fun getMonth():Int{
         return LocalDateTime.now().monthValue
     }

     fun getYear():Int{
         return LocalDateTime.now().year
     }

}