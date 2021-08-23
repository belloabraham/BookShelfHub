package com.bookshelfhub.bookshelfhub.services.payment

import com.bookshelfhub.bookshelfhub.R

class CardDrawable {

    fun getType(value:String):Int{
        return when {
            value.startsWith("4") -> {
                //Visa
                R.drawable.ic_cart
            }
            value.startsWith("2") -> {
                //master
                R.drawable.ic_cart
            }
            value.startsWith("50") -> {
                //verve
                R.drawable.ic_cart
            }
            value.startsWith("5") -> {
                //master
                R.drawable.ic_cart
            }
            value.startsWith("3") -> {
                //American express
                R.drawable.ic_cart
            }
            value.startsWith("6") -> {
                //verve
                R.drawable.ic_cart
            }
            else ->{
                0
            }
        }
    }

}