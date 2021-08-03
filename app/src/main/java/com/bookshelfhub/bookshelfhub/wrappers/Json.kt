package com.bookshelfhub.bookshelfhub.wrappers

import com.google.gson.Gson

class Json(private val jsonSerDes:Gson) {

     fun <T: Any> fromAny(obj:Any, type:Class<T>):T{
        val json = jsonSerDes.toJson(obj)
         return Gson().fromJson(json, type)
    }

}