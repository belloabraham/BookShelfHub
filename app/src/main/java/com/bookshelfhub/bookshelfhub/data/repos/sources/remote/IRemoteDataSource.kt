package com.bookshelfhub.bookshelfhub.data.repos.sources.remote

import android.app.Activity
import com.bookshelfhub.bookshelfhub.data.enums.Book
import com.bookshelfhub.bookshelfhub.data.models.entities.IEntityId
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

interface IRemoteDataSource {

    fun getListOfDataAsync(collection:String, document: String, subCollection: String): Task<QuerySnapshot>

    fun <T:Any> getLiveListOfDataAsync(collection:String, document:String, subCollection: String, type:Class<T>, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit )

    fun <T: Any> getLiveListOfDataWhereAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit)

    fun updateUserReview(userReviews: List<UserReview>, collection: String, subCollection: String, subDocument: String, bookAttrs: List<HashMap<String, FieldValue>>): Task<Void>

    fun addListOfDataAsync(collection: String, document:String, subCollection: String, list: List<Any>): Task<Void>

    fun <T: Any> getLiveOrderedBooks(activity: Activity, collection:String, userId:String, type:Class<T>, orderBy:String, direction: Query.Direction, startAfter:Timestamp, userIdKey: String = RemoteDataFields.USER_ID.KEY, downloadUrlKey:String = RemoteDataFields.DOWNLOAD_URL.KEY, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit)

    fun <T: Any> getLiveOrderedBooks(activity: Activity, collection:String, userId:String, type:Class<T>, userIdKey: String = RemoteDataFields.USER_ID.KEY, downloadUrlKey:String= RemoteDataFields.DOWNLOAD_URL.KEY, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit)

    fun updateUserReview(bookAttr: HashMap<String, FieldValue>?, userReview: UserReview, collection: String, document:String, subCollection: String, subDocument: String):Task<Void>

    fun <T: Any>  getLiveDataAsync(collection:String, document: String, type:Class<T>, retry:Boolean=true, onComplete:
        (data:T)->Unit)


    fun <T: Any> getListOfDataWhereAsync(collection:String, document:String, subCollection:String, type:Class<T>, whereKey:String, whereValue:Any, excludeDocId:String, limit:Long, orderBy: String= Book.ISBN.KEY, direction: Query.Direction = Query.Direction.DESCENDING, onComplete: (dataList:List<T>, Exception?)->Unit)


    fun getLiveDataAsync(activity: Activity, collection:String, document: String, subCollection: String, subDocument:String, shouldRetry:Boolean = true, onComplete:
        (data:DocumentSnapshot?, error: FirebaseFirestoreException?)->Unit)

    fun getListOfDataWhereAsync(collection:String, whereKey: String, whereValue: Any): Task<QuerySnapshot>


    fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String):Task<Void>


    fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String): Task<Void>

     fun addDataAsync(
        data: Any,
        collection: String,
        document: String,
        field: String
    ): Task<Void>

     fun getLiveDataAsync(activity: Activity,
        collection: String, document: String, retry:Boolean=false, onComplete:
            (data: DocumentSnapshot?, e: Exception?) -> Unit
    )

    fun <T: Any> getLiveListOfDataAsync(activity:Activity, collection:String, document:String, subCollection:String, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit)

     fun <T : Any> getListOfDataAsync(
         collection: String,
         type: Class<T>,
         orderBy: String,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    )

     fun <T : Any> getLiveListOfDataAsyncFrom(
         collection: String,
         type: Class<T>,
         startAt: Timestamp,
         direction: Query.Direction = Query.Direction.DESCENDING,
         orderBy: String = RemoteDataFields.DATE_TIME_PUBLISHED.KEY,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    )

}