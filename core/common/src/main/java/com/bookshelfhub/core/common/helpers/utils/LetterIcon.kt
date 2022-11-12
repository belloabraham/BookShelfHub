package com.bookshelfhub.core.common.helpers.utils

import com.bookshelfhub.core.common.R

object LetterIcon {

    fun getColor(value: Int): Int {
        return when {
            value.mod(2)==0 -> {
                R.color.orange
            }
            value.mod(3)==0 -> {
                R.color.light_blue_600
            }
            else -> {
                R.color.error_red
            }
        }

    }
}