package com.bookshelfhub.bookshelfhub.Utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {

        @JvmStatic
        fun dateToString(date: Date?, pattern: String?): String? {
            try {
                 return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }

        @JvmStatic
        fun stringToDate(date: String?, format: String?): Date? {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }
    }
}