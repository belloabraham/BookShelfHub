package com.bookshelfhub.bookshelfhub.Utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {
        fun dateToString(date: Date?, pattern: String?): String? {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        }

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