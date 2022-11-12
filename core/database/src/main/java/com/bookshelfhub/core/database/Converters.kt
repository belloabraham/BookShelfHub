package com.bookshelfhub.core.database

import androidx.room.TypeConverter
import java.util.*
import com.google.firebase.Timestamp

class Converters {

    @TypeConverter
    fun fromTimeStampToLong(value: Timestamp?): Long? {
       return value?.seconds?.times(1000)
    }

    @TypeConverter
    fun fromLongToTimeStamp(value: Long?): Timestamp? {
      return  if(value==null){
            null
        }else{
          val date = Date(value)
           Timestamp(date)
        }
    }
}