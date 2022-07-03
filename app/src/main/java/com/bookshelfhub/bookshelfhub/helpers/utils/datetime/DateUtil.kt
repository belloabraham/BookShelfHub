package com.bookshelfhub.bookshelfhub.helpers.utils.datetime

import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    @JvmStatic
    fun getHumanReadable(date: Date?, pattern: String?): String? {
        date?.let {
            try {
                return SimpleDateFormat(pattern, Locale.getDefault()).format(it)
            } catch (e: ParseException) {
                Timber.e(e)
                e.printStackTrace()
            }
        }

        return null
    }

    @JvmStatic
    fun stringToDate(date: String?, format: String?): Date? {
        date?.let {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(it)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return null
    }
}