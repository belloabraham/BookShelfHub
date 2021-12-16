package com.bookshelfhub.bookshelfhub.helpers

import com.google.gson.Gson
import org.json.JSONObject

/**
 * Custom Json Serializer and De-serializer
 */

class Json(private val jsonSerDes:Gson) {

     fun <T: Any> fromAny(obj:Any, type:Class<T>):T{
        val json = jsonSerDes.toJson(obj)
         return jsonSerDes.fromJson(json, type)
    }

    fun <T: Any> fromJson(json:String, type:Class<T>):T{
        return jsonSerDes.fromJson(json, type)
    }


    fun getJsonObject(value: Any): JSONObject {
        val jsonString = jsonSerDes.toJson(value)
        return JSONObject(jsonString)
    }

    fun getJsonString(value:Any): String? {
        return jsonSerDes.toJson(value)
    }

}