package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import android.annotation.SuppressLint
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.bookshelfhub.bookshelfhub.enums.DateFormat
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
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