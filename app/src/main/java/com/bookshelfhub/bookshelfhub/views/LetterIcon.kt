package com.bookshelfhub.bookshelfhub.views

import com.bookshelfhub.bookshelfhub.R

object LetterIcon {

    fun getColor(value: Int): Int {
        return if (value.mod(2)==0){
            R.color.orange
        }else if(value.mod(3)==0){
            R.color.light_blue_600
        }else{
            R.color.error_red
        }

    }
}