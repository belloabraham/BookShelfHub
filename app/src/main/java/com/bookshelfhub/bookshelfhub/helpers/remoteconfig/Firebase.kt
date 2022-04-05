package com.bookshelfhub.bookshelfhub.helpers.remoteconfig

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig

class Firebase : IRemoteConfig {
     private val remoteConfig = Firebase.remoteConfig

    override fun getString(key:String):String{
        return remoteConfig[key].asString()
    }

    override fun getBoolean(key:String):Boolean{
        return remoteConfig[key].asBoolean()
    }

    override fun getLong(key:String):Long{
        return remoteConfig[key].asLong()
    }

    override fun fetchConfigAsync(onComplete:(error:String?)->Unit){
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