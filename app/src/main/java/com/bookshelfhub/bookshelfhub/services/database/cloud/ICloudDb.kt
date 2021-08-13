package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IEntityId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings

interface ICloudDb {

    fun getLiveDataAsync(collection:String, document: String, subCollection: String, subDocument:String, shouldCache:Boolean=false, shouldRetry:Boolean = true, onComplete:
        (data:DocumentSnapshot?, error: FirebaseFirestoreException?)->Unit)

    fun <T: Any> getListOfDataAsync(collection:String, field: String, type:Class<T>, shouldCache:Boolean=false, onComplete: suspend (dataList:List<T>)->Unit)


    fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, onSuccess: suspend ()->Unit)


    fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, field:String, lastUpdated: FieldValue = FieldValue.serverTimestamp(), onSuccess: suspend ()->Unit)

     fun addDataAsync(
        data: Any,
        collection: String,
        document: String,
        field: String,
        lastUpdated: FieldValue = FieldValue.serverTimestamp(),
        onSuccess: suspend () -> Unit
    )

     fun getDataAsync(
        collection: String, document: String, shouldCache: Boolean = false, onComplete:
            (data: DocumentSnapshot?, e: Exception?) -> Unit
    )

    fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, field: String, type:Class<T>, shouldCache:Boolean = false, onComplete: suspend (dataList:List<T>)->Unit)

     fun <T : Any> getLiveListOfDataAsync(
         collection: String,
         field: String,
         type: Class<T>,
         orderBy: String = DbFields.DATE_TIME_PUBLISHED.KEY,
         shouldCache: Boolean = false,
         shouldRetry: Boolean=false,
         onComplete: (dataList: List<T>) -> Unit
    )

     fun <T : Any> getLiveListOfDataAsyncFrom(
        collection: String,
        field: String,
        type: Class<T>,
        startAt: String,
        orderBy: String = DbFields.DATE_TIME_PUBLISHED.KEY,
        shouldCache: Boolean = false,
        shouldRetry: Boolean=false,
        onComplete: (dataList: List<T>) -> Unit
    )

    fun getCacheSettings(shouldCache: Boolean): FirebaseFirestoreSettings
}