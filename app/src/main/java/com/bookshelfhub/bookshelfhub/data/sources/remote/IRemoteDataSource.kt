package com.bookshelfhub.bookshelfhub.data.sources.remote

import com.bookshelfhub.bookshelfhub.data.models.entities.IEntityId
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

interface IRemoteDataSource {


    suspend fun <T: Any> getListOfDataAsync(
        collection:String,
        document:String,
        subCollection:String,
        orderBy: String,
        direction: Query.Direction,
        startAfter:Any,
        type:Class<T>): List<T>

    suspend fun <T: Any> getListOfDataWhereAsync(
        collection:String, whereKey:String,
        whereValue:Any,
        whereKey2:String,
        whereValue2:Any,
        orderBy: String,
        direction: Query.Direction,
        type:Class<T>): List<T>

    suspend fun <T: Any>  getDataAsync(
        collection:String,
        document: String,
        type:Class<T>): T?

    suspend fun <T: Any> getListOfDataWhereAsync(
        collection:String,
        document:String,
        subCollection:String,
        type:Class<T>,
        whereKey:String,
        whereValue:Any,
        limit:Long,
        excludedDocId:String): List<T>

    suspend fun addDataAsync(
        collection:String,
        document:String,
        data: Any): Void?

    suspend fun <T: Any> getDataAsync(
        collection:String,
        document: String,
        subCollection: String,
        subDocument:String,
        shouldRetry:Boolean,
        type:Class<T>): T?

    suspend fun <T: Any> getListOfDataAsync(
        collection:String,
        document: String,
        subCollection: String,
        type:Class<T>): List<T>

   suspend fun updateUserReviews(
       userReviews: List<UserReview>,
       collection: String,
       subCollection: String,
       subDocument: String,
       bookUpdatedValues: List<HashMap<String, FieldValue>>): Void?

   suspend fun addListOfDataAsync(
       collection: String,
       document:String,
       subCollection: String,
       list: List<Any>): Void?


    suspend fun updateUserReview(
        bookUpdatedValues: HashMap<String, FieldValue>?,
        userReview: UserReview, collection: String,
        document:String, subCollection: String,
        subDocument: String): Void?

    suspend fun <T: Any> getListOfDataWhereAsync(
        collection:String,
        document:String,
        subCollection:String,
        type:Class<T>,
        whereKey:String,
        whereValue:Any,
        limit:Long,
        orderBy: String,
        direction: Query.Direction): List<T>


    suspend fun <T: Any> getListOfDataWhereAsync(
        collection:String,
        whereKey: String,
        whereValue: Any,
        type:Class<T>): List<T>

    suspend fun deleteListOfDataAsync(
        list: List<IEntityId>,
        collection: String,
        document:String,
        subCollection: String):Void


    suspend fun addListOfDataAsync(
        list: List<IEntityId>,
        collection: String,
        document:String,
        subCollection: String): Void?

    suspend fun addDataAsync(
        collection:String,
        document:String,
        field:String, data: Any): Void?


     fun <T : Any> getLiveListOfDataAsync(
         collection: String,
         type: Class<T>,
         orderBy: String,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    ):ListenerRegistration

     fun <T : Any> getLiveListOfDataAsyncFrom(
         collection: String,
         type: Class<T>,
         startAt: Timestamp,
         direction: Query.Direction = Query.Direction.DESCENDING,
         orderBy: String = RemoteDataFields.DATE_TIME_PUBLISHED,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    ):ListenerRegistration

}