package com.bookshelfhub.bookshelfhub.helpers

import com.google.gson.Gson
import org.json.JSONObject

class Json(private val jsonSerDes:Gson) {


     fun <T: Any> fromAny(obj:Any, type:Class<T>):T{
        val json = jsonSerDes.toJson(obj)
         return jsonSerDes.fromJson(json, type)
    }

    fun getJsonObject(value: Any): JSONObject {
        val jsonString = jsonSerDes.toJson(value)
        return JSONObject(jsonString)
    }

}