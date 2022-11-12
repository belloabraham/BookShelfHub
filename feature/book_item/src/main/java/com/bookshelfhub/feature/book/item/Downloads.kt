package com.bookshelfhub.feature.book.item

object Downloads {

     fun getHumanReadable(value:Long): String {
        val thousand = 1000
        val billion = 1000000000
        val million = 1000000
        val remainder:Int
        val main:Long

        val unit:String = when {
            value>=billion -> {
                remainder = value.mod(billion)
                main = (value - remainder)/billion
                "B"
            }
            value >= million -> {
                remainder = value.mod(million)
                main = (value - remainder)/million
                "M"
            }
            value >= thousand -> {
                remainder = value.mod(thousand)
                main = (value - remainder)/thousand
                "K"
            }
            else -> {
                main = value
                ""
            }
        }
        val unitPlus = if (unit.isNotBlank()){
            "$unit+"
        }else{
            unit
        }
        return "$main"+unitPlus
    }


}