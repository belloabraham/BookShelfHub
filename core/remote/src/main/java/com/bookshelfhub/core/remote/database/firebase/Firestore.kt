package com.bookshelfhub.core.remote.database.firebase

import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.model.entities.IEntityId
import com.bookshelfhub.core.model.entities.UserReview
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject


 class Firestore @Inject constructor(
     private val json: Json,
     private val connectionUtil: ConnectionUtil,
     ): IRemoteDataSource {
    private val db:FirebaseFirestore = Firebase.firestore
     
     init {
         db.firestoreSettings =  firestoreSettings {
             isPersistenceEnabled=false
         }
     }


    override suspend fun addDataAsync(
        collection:String,
        document:String,
        field:String,
        data: Any): Void? {
        val newData = hashMapOf(
            field to data,
        )
        throwNoInternetConnectionError()
          return  db.collection(collection)
                .document(document)
                .set(newData, SetOptions.merge()).await()

    }

     override suspend fun updateDocData(
         collection:String,
         document:String,
         field:String,
         value:Any,
     ): Void? {
         throwNoInternetConnectionError()
         return db.collection(collection).document(document).update(field, value).await()
     }

     override suspend fun addDataAsync(
         collection:String,
         document:String,
         data: Any): Void? {
         throwNoInternetConnectionError()
         return  db.collection(collection)
             .document(document)
             .set(data, SetOptions.merge()).await()
     }

     override suspend fun <T: Any> getDataAsync(
         collection:String,
         document: String,
         type:Class<T>): T? {
         throwNoInternetConnectionError()
         val docSnapshot =  db.collection(collection)
             .document(document).get().await()
        return docSnapshotToType(docSnapshot, type)
     }


     override suspend fun <T: Any> getDataAsync(
         collection:String,
         document: String,
         subCollection: String,
         subDocument:String,
         type:Class<T>): T? {
         throwNoInternetConnectionError()
         val documentSnapshot = db.collection(collection).document(document).collection(subCollection).document(subDocument).get().await()
        return docSnapshotToType(documentSnapshot, type)
     }


     override suspend fun <T: Any> getListOfDataWhereAsync(
         collection:String,
         whereKey: String,
         whereValue: Any,
         type:Class<T>): List<T> {
         throwNoInternetConnectionError()
       val querySnapShot = db.collection(collection).whereEqualTo(whereKey, whereValue).get().await()
       return querySnapshotToListOfType(querySnapShot, type)
     }


     override suspend fun <T: Any> getListOfDataWhereAsync(
         collection:String,
         document:String,
         subCollection:String,
         type:Class<T>,
         whereKey:String,
         whereValue:Any,
         limit:Long,
         excludedDocId:String): List<T> {
         throwNoInternetConnectionError()
         val querySnapShot =  db.collection(collection).document(document).collection(subCollection)
             .whereEqualTo(whereKey,whereValue)
             .limit(limit)
             .get().await()

         querySnapShot.removeAll {
             it.id == excludedDocId
         }
         return querySnapshotToListOfType(querySnapShot, type)
     }


     override suspend fun <T: Any> getListOfDataWhereAsync(
         collection:String,
         whereKey:String,
         whereValue:Any,
         whereKey2:String,
         whereValue2:Any,
         orderBy: String,
         direction: Query.Direction,
         type:Class<T>): List<T> {
         throwNoInternetConnectionError()
         val querySnapShot =  db.collection(collection)
             .whereEqualTo(whereKey,whereValue)
             .whereEqualTo(whereKey2,whereValue2)
             .orderBy(orderBy, direction)
             .get().await()

         return querySnapshotToListOfType(querySnapShot, type)
     }

     override suspend fun <T: Any> getListOfDataWhereAsync(
         collection:String,
         whereKey:String,
         whereValue:Any,
         whereKey2:String,
         whereValue2:Any,
         orderBy: String,
         startAt:Int,
         direction: Query.Direction,
         type:Class<T>): List<T> {
        throwNoInternetConnectionError()
         val querySnapShot =  db.collection(collection)
             .whereEqualTo(whereKey,whereValue)
             .whereEqualTo(whereKey2,whereValue2)
             .orderBy(orderBy, direction)
             .startAt(startAt)
             .get().await()

         return querySnapshotToListOfType(querySnapShot, type)
     }


     override suspend fun <T: Any> getListOfDataWhereAsync(
         collection:String,
         document:String,
         subCollection:String,
         type:Class<T>,
         whereKey:String, whereValue:Any,
         limit:Long, orderBy: String,
         direction: Query.Direction): List<T> {
         throwNoInternetConnectionError()
         val querySnapShot =  db.collection(collection).document(document).collection(subCollection)
             .whereEqualTo(whereKey,whereValue)
             .orderBy(orderBy, direction)
             .limit(limit)
             .get().await()

         return querySnapshotToListOfType(querySnapShot, type)
     }

     override suspend fun <T: Any> getListOfDataAsync(
         collection:String,
         document:String,
         subCollection:String,
         orderBy: String,
         direction: Query.Direction,
         startAfter:Any,
         type:Class<T>): List<T> {
         throwNoInternetConnectionError()
         val querySnapshot = db.collection(collection).document(document).collection(subCollection)
             .orderBy(orderBy, direction)
             .startAfter(startAfter)
             .get().await()
         return   querySnapshotToListOfType(querySnapshot, type)
     }

     override suspend fun <T: Any> getListOfDataAsync(
         collection:String, document:String,
         subCollection:String,
         type:Class<T>): List<T> {
         throwNoInternetConnectionError()
         val querySnapshot = db.collection(collection).document(document).collection(subCollection)
             .get().await()
         return   querySnapshotToListOfType(querySnapshot, type)
     }


     override suspend fun updateUserReview(
         bookUpdatedValues: HashMap<String, FieldValue>?,
         userReview: UserReview,
         collection: String,
         document:String,
         subCollection: String,
         subDocument: String): Void? {
         throwNoInternetConnectionError()
         return db.runBatch { batch->
             val reviewDocRef = db.collection(collection).document(document).collection(subCollection).document(subDocument)
             val bookDynamicAttrDocRef = db.collection(collection).document(document)

             val reviewDate = hashMapOf(
                 RemoteDataFields.REVIEW_DATE_TIME to FieldValue.serverTimestamp()
             )
             batch.set(reviewDocRef, userReview)
             batch.set(reviewDocRef, reviewDate, SetOptions.merge())
             bookUpdatedValues?.let {
                 batch.set(bookDynamicAttrDocRef, it, SetOptions.merge())
             }
         }.await()

     }


     override suspend fun updateUserReviews(
         userReviews: List<UserReview>,
         collection: String,
         subCollection: String,
         subDocument: String,
         bookUpdatedValues: List<HashMap<String, FieldValue>>): Void? {
         throwNoInternetConnectionError()
       return  db.runBatch { batch->
             val length = userReviews.size - 1
             for (i in 0..length){
                 val reviewDocRef = db.collection(collection).document(userReviews[i].bookId).collection(subCollection).document(subDocument)
                 val bookDynamicAttrDocRef = db.collection(collection).document(userReviews[i].bookId)
                 batch.set(reviewDocRef, userReviews[i], SetOptions.merge())
                 batch.set(bookDynamicAttrDocRef, bookUpdatedValues[i], SetOptions.merge())
             }
         }.await()

     }


     override suspend fun addListOfDataAsync(
         collection: String,
         document:String,
         subCollection: String,
         list: List<Any>): Void? {
         throwNoInternetConnectionError()
        return  db.runBatch { batch->
             for (item in list){
                 val docRef = db.collection(collection).document(document).collection(subCollection).document()
                 batch.set(docRef, item)
             }
         }.await()
     }


     override suspend fun addListOfDataAsync(
         list: List<IEntityId>,
         collection: String,
         document:String,
         subCollection: String): Void? {
         throwNoInternetConnectionError()
       return  db.runBatch { batch->
             for (item in list){
                 val docRef = db.collection(collection).document(document).collection(subCollection).document("${item.id}")
                 batch.set(docRef, item)
             }
         }.await()

    }

     override suspend fun deleteListOfDataAsync(
         list: List<IEntityId>,
         collection: String,
         document:String,
         subCollection: String):Void {
         throwNoInternetConnectionError()
        return db.runBatch { batch->
             for (item in list){
                 val docRef = db.collection(collection).document(document).collection(subCollection).document("${item.id}")
                 batch.delete(docRef)
             }
         }.await()

     }

     private fun <T: Any> docSnapshotToType(
         documentSnapshot: DocumentSnapshot?,
         type:Class<T>): T?{
         if(documentSnapshot != null && documentSnapshot.exists()){
             val data =  documentSnapshot.data!!
            return  json.fromAny(data, type)
         }
         return null
     }

    private fun <T: Any> querySnapshotToListOfType(
        querySnapshot: QuerySnapshot?,
        type:Class<T>): List<T> {
         var dataList = emptyList<T>()

         querySnapshot?.let {
             if (!it.isEmpty) {
                 for (doc in it) {
                     if (doc.exists()) {
                         val data = doc.data
                         dataList = dataList.plus(json.fromAny(data, type))
                     }
                 }
             }
         }
         return dataList
     }

     private fun throwNoInternetConnectionError(){
         val noInternetConnection = !connectionUtil.isConnected()
         if(noInternetConnection){
             throw IOException("No internet connection")
         }
     }
}