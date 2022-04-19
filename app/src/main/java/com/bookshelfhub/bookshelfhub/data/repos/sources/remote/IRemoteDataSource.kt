package com.bookshelfhub.bookshelfhub.data.repos.sources.remote

import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.entities.IEntityId
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

interface IRemoteDataSource {


    suspend fun <T: Any>  getDataAsync(collection:String, document: String, type:Class<T>): T?

    suspend fun <T: Any> getListOfDataWhereAsync(collection:String, document:String, subCollection:String, type:Class<T>,  whereKey:String, whereValue:Any, limit:Long, excludedDocId:String): List<T>

    suspend fun getDataAsync(collection:String, document: String): DocumentSnapshot

    suspend fun <T: Any> getDataAsync(collection:String, document: String, subCollection: String, subDocument:String, shouldRetry:Boolean,type:Class<T>): T?

    suspend fun <T: Any> getListOfDataAsync(collection:String, document: String, subCollection: String, type:Class<T>): List<T>

    fun <T:Any> getLiveListOfDataAsync(collection:String, document:String, subCollection: String, type:Class<T>, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit ): ListenerRegistration


   suspend fun updateUserReviews(userReviews: List<UserReview>, collection: String, subCollection: String, subDocument: String, bookUpdatedValues: List<HashMap<String, FieldValue>>): Void?

    fun addListOfDataAsync(collection: String, document:String, subCollection: String, list: List<Any>): Task<Void>


    fun <T: Any> getLiveOrderedBooks(collection:String, userId:String, type:Class<T>, userIdKey: String = RemoteDataFields.USER_ID, downloadUrlKey:String= RemoteDataFields.DOWNLOAD_URL, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit) : ListenerRegistration

    suspend fun updateUserReview(bookUpdatedValues: HashMap<String, FieldValue>?, userReview: UserReview, collection: String, document:String, subCollection: String, subDocument: String): Void?

    suspend fun <T: Any> getListOfDataWhereAsync(collection:String, document:String, subCollection:String, type:Class<T>,  whereKey:String, whereValue:Any,limit:Long, orderBy: String, direction: Query.Direction): List<T>


    suspend fun <T: Any> getListOfDataWhereAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>): List<T>


    fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String):Task<Void>


    fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String): Task<Void>

     fun addDataAsync(
        data: Any,
        collection: String,
        document: String,
        field: String
    ): Task<Void>


    fun <T: Any> getLiveListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit): ListenerRegistration

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