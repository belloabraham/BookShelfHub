package com.bookshelfhub.bookshelfhub.wrapper

import com.google.gson.Gson

class Json {

    fun <T: Any> fromJson(json:String, type:Class<T>):T{
         return Gson().fromJson(json, type)
    }

}