package com.bookshelfhub.core.remote.database

import com.bookshelfhub.core.model.entities.IEntityId
import com.bookshelfhub.core.model.entities.UserReview
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

interface IRemoteDataSource {

    fun updateDocDataAsync(
        collection:String,
        document:String,
        field:String,
        value:Any,
    ): Task<Void>

    suspend fun updateDocData(
        collection:String,
        document:String,
        field:String,
        value:Any,
    ): Void?

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

    suspend fun <T: Any> getListOfDataWhereAsync(
        collection:String,
        whereKey:String,
        whereValue:Any,
        whereKey2:String,
        whereValue2:Any,
        orderBy: String,
        startAt:Int,
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
       bookUpdatedValues: List<Map<String, FieldValue>>): Void?

   suspend fun addListOfDataAsync(
       collection: String,
       document:String,
       subCollection: String,
       list: List<Any>): Void?


    suspend fun updateUserReview(
        bookUpdatedValues: Map<String, FieldValue>?,
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


}