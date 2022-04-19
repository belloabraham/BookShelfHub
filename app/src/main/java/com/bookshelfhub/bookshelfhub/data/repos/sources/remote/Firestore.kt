package com.bookshelfhub.bookshelfhub.data.repos.sources.remote

import com.bookshelfhub.bookshelfhub.data.models.entities.IEntityId
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


 class Firestore @Inject constructor(val json: Json): IRemoteDataSource {
    private val db:FirebaseFirestore = Firebase.firestore
     
     init {
         //Disable firestore caching
         db.firestoreSettings =  firestoreSettings {
             isPersistenceEnabled=false
         }
     }


    override fun <T:Any> getLiveListOfDataAsync(collection:String, document:String, subCollection: String, type:Class<T>, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit ): ListenerRegistration {
     val subscription = db.collection(collection).document(document).collection(subCollection)
             .addSnapshotListener{ querySnapShot, e ->
                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 val dataList = querySnapshotToListOfType(querySnapShot, type)
                 onComplete(dataList)
             }
        return subscription
     }


    override fun addDataAsync(data: Any, collection:String, document:String, field:String ): Task<Void> {
        val newData = hashMapOf(
            field to data,
        )
          return  db.collection(collection)
                .document(document)
                .set(newData, SetOptions.merge())

    }

     override fun <T: Any> getLiveListOfDataAsync(collection:String, type:Class<T>, orderBy:String, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit): ListenerRegistration {
         val subscription = db.collection(collection)
             .orderBy(orderBy)
             .addSnapshotListener { querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 val dataList = querySnapshotToListOfType(querySnapShot, type)
                 onComplete(dataList)
             }
         return subscription
     }


     override suspend fun getDataAsync(collection:String, document: String): DocumentSnapshot{
         return  db.collection(collection)
             .document(document).get().await()
     }

     override suspend fun <T: Any>  getDataAsync(collection:String, document: String, type:Class<T>): T? {
         val docSnapshot =  db.collection(collection)
             .document(document).get().await()
        return docSnapshotToType(docSnapshot, type)
     }


     override suspend fun <T: Any> getDataAsync(collection:String, document: String, subCollection: String, subDocument:String, shouldRetry:Boolean,type:Class<T>): T? {

         val documentSnapshot = db.collection(collection).document(document).collection(subCollection).document(subDocument).get().await()

        return docSnapshotToType(documentSnapshot, type)

     }


     override fun <T: Any> getLiveListOfDataAsyncFrom(collection:String, type:Class<T>, startAt: Timestamp, direction: Query.Direction, orderBy:String, shouldRetry: Boolean, onComplete:  (dataList:List<T>)->Unit): ListenerRegistration {

         val subscription = db.collection(collection)
             .orderBy(orderBy, direction)
             .startAfter(startAt)
             .addSnapshotListener { querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }
                 val dataList = querySnapshotToListOfType(querySnapShot, type)
                 onComplete(dataList)
             }
        return subscription
     }


     override suspend fun <T: Any> getListOfDataWhereAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>): List<T> {
       val querySnapShot =  db.collection(collection)
             .whereEqualTo(whereKey, whereValue)
             .get().await()
         return querySnapshotToListOfType(querySnapShot, type)
     }


     override fun <T: Any> getLiveOrderedBooks(collection:String, userId:String, type:Class<T>, userIdKey: String, downloadUrlKey:String, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit): ListenerRegistration {
         val subscription = db.collection(collection)
             .whereNotEqualTo(downloadUrlKey, null)
             .whereEqualTo(userIdKey, userId)
             .addSnapshotListener{ querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 val dataList = querySnapshotToListOfType(querySnapShot, type)
                 onComplete(dataList)
             }
         return subscription
     }


     override suspend fun <T: Any> getListOfDataWhereAsync(collection:String, document:String, subCollection:String, type:Class<T>,  whereKey:String, whereValue:Any, limit:Long, excludedDocId:String): List<T> {

         val querySnapShot =  db.collection(collection).document(document).collection(subCollection)
             .whereEqualTo(whereKey,whereValue)
             .limit(limit)
             .get().await()

         querySnapShot.removeAll {
             it.id == excludedDocId
         }

         return querySnapshotToListOfType(querySnapShot, type)

     }


     override suspend fun <T: Any> getListOfDataWhereAsync(collection:String, document:String, subCollection:String, type:Class<T>,  whereKey:String, whereValue:Any, limit:Long, orderBy: String, direction: Query.Direction): List<T> {

        val querySnapShot =  db.collection(collection).document(document).collection(subCollection)
             .whereEqualTo(whereKey,whereValue)
             .orderBy(orderBy, direction)
             .limit(limit)
             .get().await()

         return querySnapshotToListOfType(querySnapShot, type)

     }

     override suspend fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>): List<T> {

         val querySnapshot = db.collection(collection).document(document).collection(subCollection)
             .get().await()
         return   querySnapshotToListOfType(querySnapshot, type)
     }


     override fun <T: Any> getLiveListOfDataAsync(collection:String, document:String, subCollection:String, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit): ListenerRegistration {

         val subscription = db.collection(collection).document(document).collection(subCollection)
            .addSnapshotListener{ querySnapShot, error ->
                if (error!=null && shouldRetry){
                    return@addSnapshotListener
                }
                val dataList = querySnapshotToListOfType(querySnapShot, type)
                runBlocking {
                    onComplete(dataList)
                }
            }
        return subscription
    }


     override suspend fun updateUserReview(bookUpdatedValues: HashMap<String, FieldValue>?, userReview: UserReview, collection: String, document:String, subCollection: String, subDocument: String): Void? {

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


     override suspend fun updateUserReviews(userReviews: List<UserReview>, collection: String, subCollection: String, subDocument: String, bookUpdatedValues: List<HashMap<String, FieldValue>>): Void? {

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


     override fun addListOfDataAsync(collection: String, document:String, subCollection: String, list: List<Any>): Task<Void> {

        return  db.runBatch { batch->
             for (item in list){
                 val docRef = db.collection(collection).document(document).collection(subCollection).document()
                 batch.set(docRef, item)
             }
         }
     }


     override fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String): Task<Void> {

       return  db.runBatch { batch->
             for (item in list){
                 val docRef = db.collection(collection).document(document).collection(subCollection).document("${item.id}")
                 batch.set(docRef, item)
             }
         }

    }

     override fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String): Task<Void> {

        return db.runBatch { batch->
             for (item in list){
                 val docRef = db.collection(collection).document(document).collection(subCollection).document("${item.id}")
                 batch.delete(docRef)
             }
         }

     }


     private fun <T: Any> docSnapshotToType(documentSnapshot: DocumentSnapshot?, type:Class<T>): T?{
         if(documentSnapshot != null && documentSnapshot.exists()){
             val data =  documentSnapshot.data!!
            return  json.fromAny(data, type)
         }
         return null
     }

    private fun <T: Any> querySnapshotToListOfType(querySnapshot: QuerySnapshot?, type:Class<T>): List<T> {
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

}