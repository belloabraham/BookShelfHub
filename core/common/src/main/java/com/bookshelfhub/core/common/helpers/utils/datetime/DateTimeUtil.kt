package com.bookshelfhub.core.common.helpers.utils.datetime

import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


object DateTimeUtil {

    /**
    * Gets the device time zone in the format of
    * e.g GMT - Africa/Lagos
    */
     fun getTimeZone(): String {
       return ZoneId.systemDefault().toString()
     }

     fun getDateTimeAsString():String{
         return LocalDateTime.now().toString()
     }

     fun getMonth():Int{
         return LocalDateTime.now().monthValue
     }

     fun getYear():Int{
         return LocalDateTime.now().year
     }

     fun initialize(context:Context){
         AndroidThreeTen.init(context)
     }

}