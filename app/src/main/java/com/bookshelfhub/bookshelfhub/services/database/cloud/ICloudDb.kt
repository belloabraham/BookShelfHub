package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IEntityId
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

interface ICloudDb {


    fun addListOfDataAsync(collection: String, document:String, subCollection: String, list: List<Any>, onSuccess: suspend ()->Unit)

    fun <T: Any> getOrderedBooks(collection:String, userId:String, type:Class<T>, orderBy:String, startAfter:Timestamp, userIdKey: String = DbFields.USER_ID.KEY, downloadUrlKey:String = DbFields.DOWNLOAD_URL.KEY, shouldCache:Boolean = false, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit)

    fun <T: Any> getOrderedBooks(collection:String, userId:String, type:Class<T>, userIdKey: String = DbFields.USER_ID.KEY, downloadUrlKey:String=DbFields.DOWNLOAD_URL.KEY, shouldCache:Boolean = false, shouldRetry: Boolean = true, onComplete: (dataList:List<T>)->Unit)

    fun updateUserReview(bookAttr: HashMap<String, FieldValue>, userReview: UserReview, collection: String, document:String, subCollection: String, subDocument: String, onSuccess: suspend ()->Unit)

    fun <T: Any>  getLiveDataAsync(collection:String, document: String, type:Class<T>, shouldCache:Boolean = false, retry:Boolean=true, onComplete:
        (data:T)->Unit)


    fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>, whereKey:String, whereValue:Any, excludeDocId:String, limit:Long, orderBy: String=Book.ISBN.KEY, direction: Query.Direction = Query.Direction.DESCENDING, shouldCache:Boolean = false, onComplete: (dataList:List<T>, Exception?)->Unit)


    fun getLiveDataAsync(collection:String, document: String, subCollection: String, subDocument:String,shouldCache:Boolean=false, shouldRetry:Boolean = true, onComplete:
        (data:DocumentSnapshot?, error: FirebaseFirestoreException?)->Unit)

    fun <T: Any> getListOfDataAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>, shouldCache:Boolean=false, onComplete: suspend (dataList:List<T>)->Unit)


    fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, onSuccess: suspend ()->Unit)


    fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, onSuccess: suspend ()->Unit)

     fun addDataAsync(
        data: Any,
        collection: String,
        document: String,
        field: String,
        onSuccess: suspend () -> Unit
    )

     fun getLiveDataAsync(
        collection: String, document: String, shouldCache: Boolean = false, retry:Boolean=false, onComplete:
            (data: DocumentSnapshot?, e: Exception?) -> Unit
    )

    fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>, shouldCache:Boolean = false, shouldRetry: Boolean = false, onComplete: suspend (dataList:List<T>)->Unit)

     fun <T : Any> getListOfDataAsync(
         collection: String,
         type: Class<T>,
         orderBy: String,
         shouldCache: Boolean = false,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    )

     fun <T : Any> getLiveListOfDataAsyncFrom(
         collection: String,
         type: Class<T>,
         startAt: Timestamp,
         orderBy: String = DbFields.DATE_TIME_PUBLISHED.KEY,
         shouldCache: Boolean = false,
         shouldRetry: Boolean = true,
         onComplete: (dataList: List<T>) -> Unit
    )

    fun getCacheSettings(shouldCache: Boolean): FirebaseFirestoreSettings
}