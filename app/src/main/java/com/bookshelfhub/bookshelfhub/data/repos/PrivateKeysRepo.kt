package com.bookshelfhub.bookshelfhub.data.repos

import android.app.Activity
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PrivateKeysRepo(private val databaseRef: DatabaseReference) {

     suspend fun <T : Any> getPrivateKeys(
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