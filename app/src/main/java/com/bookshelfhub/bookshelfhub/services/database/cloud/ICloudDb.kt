package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IEntityId
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

interface ICloudDb {

    fun <T: Any> getListOfDataAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit)

    fun updateUserReview(userReviews: List<UserReview>, collection: String, subCollection: String, subDocument: String, bookAttrs: List<HashMap<String, FieldValue>>): Task<Void>

    fun addListOfDataAsync(collection: String, document:String, subCollection: String, list: List<Any>): Task<Void>

    fun <T: Any> getOrderedBooks(collection:String, userId:String, type:Class<T>, orderBy:String, direction: Query.Direction, startAfter:Timestamp, userIdKey: String = DbFields.USER_ID.KEY, downloadUrlKey:String = DbFields.DOWNLOAD_URL.KEY, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit)

    fun <T: Any> getOrderedBooks(collection:String, userId:String, type:Class<T>, userIdKey: String = DbFields.USER_ID.KEY, downloadUrlKey:String=DbFields.DOWNLOAD_URL.KEY, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit)

    fun updateUserReview(bookAttr: HashMap<String, FieldValue>?, userReview: UserReview, collection: String, document:String, subCollection: String, subDocument: String): Task<Void>

    fun <T: Any>  getLiveDataAsync(collection:String, document: String, type:Class<T>, retry:Boolean=true, onComplete:
        (data:T)->Unit)


    fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>, whereKey:String, whereValue:Any, excludeDocId:String, limit:Long, orderBy: String= Book.ISBN.KEY, direction: Query.Direction = Query.Direction.DESCENDING, onComplete: (dataList:List<T>, Exception?)->Unit)


    fun getLiveDataAsync(collection:String, document: String, subCollection: String, subDocument:String,shouldRetry:Boolean = true, onComplete:
        (data:DocumentSnapshot?, error: FirebaseFirestoreException?)->Unit)

    fun <T: Any> getListOfDataAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>, onComplete: suspend (dataList:List<T>)->Unit)


    fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, onSuccess: suspend ()->Unit)


    fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, onSuccess: suspend ()->Unit)

     fun addDataAsync(
        data: Any,
        collection: String,
        document: String,
        field: String,
        onSuccess: suspend () -> Unit
    ): Task<Void>

     fun getLiveDataAsync(
        collection: String, document: String, retry:Boolean=false, onComplete:
            (data: DocumentSnapshot?, e: Exception?) -> Unit
    )

    fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit)

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
         orderBy: String = DbFields.DATE_TIME_PUBLISHED.KEY,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    )

}