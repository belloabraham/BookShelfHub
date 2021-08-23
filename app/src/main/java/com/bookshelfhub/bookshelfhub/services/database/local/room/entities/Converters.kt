package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.*

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