package com.bookshelfhub.bookshelfhub.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig

open class FirebaseRemoteConfig {
    private val remoteConfig = Firebase.remoteConfig

    open fun getString(key:String):String{
        return remoteConfig[key].asString()
    }

    open fun getBoolean(key:String):Boolean{
        return remoteConfig[key].asBoolean()
    }

    open fun getLong(key:String):Long{
        return remoteConfig[key].asLong()
    }

    open fun fetchConfigAsync(onComplete:(error:String?)->Unit){
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    onComplete(null)
                }else{
                    onComplete(it.exception!!.message.toString())
                }
            }
    }

}