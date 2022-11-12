package com.bookshelfhub.core.common.helpers.utils.datetime

import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
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

     fun initialize(context:Context){
         AndroidThreeTen.init(context)
     }

}