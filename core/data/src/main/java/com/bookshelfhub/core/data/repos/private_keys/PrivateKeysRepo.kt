package com.bookshelfhub.core.data.repos.private_keys

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class PrivateKeysRepo() : IPrivateKeysRepo {

    private val databaseRef = Firebase.database.reference

     override suspend fun <T : Any> getPrivateKeys(
        key: String,
        type: Class<T>,
    ): T? {
            val dataSnapshot = databaseRef.child(key).get().await()
            return   if(dataSnapshot.exists()){
                dataSnapshot.getValue(type)
            }else{
                null
            }
    }
}