package com.bookshelfhub.bookshelfhub.services

import android.app.Activity
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.runBlocking

class PrivateKeys {

   private val database = Firebase.database.reference

    fun <T:Any>  get(key:String, type:Class<T>, onSuccess: (keys:T?)->Unit) {
        database.child(key).get().addOnSuccessListener{
         val data =  it.getValue(type)
                onSuccess(data)
       }
    }

    companion object{
        const val API_KEYS="api_keys"
        const val FIXER_ENDPOINT="fixerEndpoint"
        const val PAYSTACK_LIVE_PUB_KEY="payStackLivePublicKey"
        const val PAYSTACK_LIVE_PRI_KEY="payStackLivePrivateKey"
        const val PERSPECTIVE_API_KEY="perspectiveKey"
    }

}