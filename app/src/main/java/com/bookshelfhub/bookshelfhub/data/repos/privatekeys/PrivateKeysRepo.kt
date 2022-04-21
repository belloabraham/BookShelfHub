package com.bookshelfhub.bookshelfhub.data.repos.privatekeys

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PrivateKeysRepo(private val databaseRef: DatabaseReference) :
    IPrivateKeysRepo {

     override suspend fun <T : Any> getPrivateKeys(
        key: String,
        type: Class<T>,
    ): T? {
        return  withContext(IO){
            val dataSnapshot = databaseRef.child(key).get().await()
            if(dataSnapshot.exists()){
                dataSnapshot.getValue(type)
            }
            null
        }

    }
}