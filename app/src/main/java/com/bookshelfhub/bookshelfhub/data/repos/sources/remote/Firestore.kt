package com.bookshelfhub.bookshelfhub.data.repos.sources.remote

import android.app.Activity
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
import javax.inject.Inject


 class Firestore @Inject constructor(val json: Json): IRemoteDataSource {
    private val db:FirebaseFirestore = Firebase.firestore
     
     init {
         //Disable firestore caching
         db.firestoreSettings =  firestoreSettings {
             isPersistenceEnabled=false
         }
     }


    override fun <T:Any> getLiveListOfDataAsync(collection:String, document:String, subCollection: String, type:Class<T>, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit ){
         db.collection(collection).document(document).collection(subCollection)
             .addSnapshotListener{ querySnapShot, e ->
                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }
                 var dataList = emptyList<T>()
                 querySnapShot?.let {
                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 val data =  doc.data
                                 dataList = dataList.plus(json.fromAny(data, type))
                             }
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


    override fun addDataAsync(data: Any, collection:String, document:String, field:String ): Task<Void> {
        val newData = hashMapOf(
            field to data,
        )
          return  db.collection(collection)
                .document(document)
                .set(newData, SetOptions.merge())

    }

     override fun <T: Any> getListOfDataAsync(collection:String, type:Class<T>, orderBy:String, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit){
         db.collection(collection)
             .orderBy(orderBy)
             .addSnapshotListener { querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 var dataList = emptyList<T>()
                 querySnapShot?.let {

                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 val data =  doc.data
                                 dataList = dataList.plus(json.fromAny(data, type))
                             }
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


     override fun <T: Any>  getLiveDataAsync(collection:String, document: String, type:Class<T>,  retry:Boolean, onComplete:
         (data:T)->Unit){
         
         db.collection(collection).document(document)
             .addSnapshotListener { documentSnapShot, error ->
                 if (retry && error!=null){
                     return@addSnapshotListener
                 }
                 documentSnapShot?.let {
                    if (it.exists()){
                        val data =  it.data!!
                        val model = json.fromAny(data, type)
                        onComplete(model)
                    }
                 }
             }
     }


   // open fun <T: Any> getDataAsync(collection:String, document: String, field:String, type:Class<T>, onComplete:
   override fun getLiveDataAsync(activity: Activity, collection:String, document: String, retry:Boolean, onComplete:
     (data:DocumentSnapshot?, e:Exception?)->Unit){
       
        db.collection(collection)
            .document(document)
            .addSnapshotListener(activity) { documentSnapShot, error ->

                if (retry && error!=null){
                    return@addSnapshotListener
                }
                onComplete(documentSnapShot, error)
            }
    }


     override fun getLiveDataAsync(activity: Activity, collection:String, document: String, subCollection: String, subDocument:String, shouldRetry:Boolean, onComplete:
         (data:DocumentSnapshot?, error:FirebaseFirestoreException?)->Unit){
         
         db.collection(collection).document(document).collection(subCollection).document(subDocument)
             .addSnapshotListener(activity){ documentSnapShot, error ->
                 if (shouldRetry && error!=null){
                     return@addSnapshotListener
                 }
                     onComplete(documentSnapShot, error)
             }
     }

     override fun <T: Any> getLiveListOfDataAsyncFrom(collection:String, type:Class<T>, startAt: Timestamp, direction: Query.Direction, orderBy:String, shouldRetry: Boolean, onComplete:  (dataList:List<T>)->Unit){

         
         db.collection(collection)
             .orderBy(orderBy, direction)
             .startAfter(startAt)
             .addSnapshotListener { querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 var dataList = emptyList<T>()
                 querySnapShot?.let {
                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 val data =  doc.data
                                 dataList = dataList.plus(json.fromAny(data, type))
                             }
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


     override fun getListOfDataAsync(collection:String, document: String, subCollection: String): Task<QuerySnapshot> {
        return  db.collection(collection).document(document).collection(subCollection)
             .get()
     }



     override fun <T: Any> getLiveListOfDataWhereAsync(collection:String, whereKey: String, whereValue: Any, type:Class<T>, shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit){

         db.collection(collection)
             .whereEqualTo(whereKey, whereValue)
             .addSnapshotListener { querySnapShot, error  ->

                 if (error!=null && shouldRetry){
                     return@addSnapshotListener
                 }

                 var dataList = emptyList<T>()
                 querySnapShot?.let {
                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 val data =  doc.data
                                 dataList = dataList.plus(json.fromAny(data, type))
                             }
                         }
                     }
                 }

                 runBlocking {
                     onComplete(dataList)
                 }
             }
     }

     override fun getListOfDataWhereAsync(collection:String, whereKey: String, whereValue: Any): Task<QuerySnapshot> {
         
       return  db.collection(collection)
             .whereEqualTo(whereKey, whereValue)
             .get()
     }


     //TODO Sub Collections
     override fun <T: Any> getLiveOrderedBooks(activity: Activity, collection:String, userId:String, type:Class<T>, orderBy:String, direction: Query.Direction, startAfter:Timestamp, userIdKey: String, downloadUrlKey:String, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit){
         
         db.collection(collection)
             .whereNotEqualTo(downloadUrlKey, null)
             .whereEqualTo(userIdKey, userId)
             .orderBy(orderBy, direction)
             .startAfter(startAfter)
             .addSnapshotListener(activity) { querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 var dataList = emptyList<T>()
                 querySnapShot?.let {

                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 val data =  doc.data
                                 dataList = dataList.plus(json.fromAny(data, type))
                             }
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


     override fun <T: Any> getLiveOrderedBooks(activity: Activity, collection:String, userId:String, type:Class<T>, userIdKey: String, downloadUrlKey:String, shouldRetry: Boolean, onComplete: (dataList:List<T>)->Unit){
         db.collection(collection)
             .whereNotEqualTo(downloadUrlKey, null)
             .whereEqualTo(userIdKey, userId)
             .addSnapshotListener(activity) { querySnapShot, e ->

                 if (shouldRetry && e!=null){
                     return@addSnapshotListener
                 }

                 var dataList = emptyList<T>()
                 querySnapShot?.let {

                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 val data =  doc.data
                                 dataList = dataList.plus(json.fromAny(data, type))
                             }
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


     override fun <T: Any> getListOfDataWhereAsync(collection:String, document:String, subCollection:String, type:Class<T>,  whereKey:String, whereValue:Any, excludeDocId:String, limit:Long, orderBy: String, direction: Query.Direction,  onComplete: (dataList:List<T>, Exception?)->Unit){

         var dataList = emptyList<T>()
         db.collection(collection).document(document).collection(subCollection)
             .whereEqualTo(whereKey,whereValue)
             .orderBy(orderBy, direction)
             .limit(limit)
             .get()
             .addOnSuccessListener {
                     if (!it.isEmpty){
                         for (doc in it) {
                             if (doc.exists()){
                                 if (doc.id!=excludeDocId){
                                     val data =  doc.data
                                     dataList = dataList.plus(json.fromAny(data, type))
                                 }
                             }
                         }
                     }
                 onComplete(dataList, null)
             }
             .addOnFailureListener {
                 onComplete(dataList, it)
             }
     }


     override fun <T: Any> getLiveListOfDataAsync(activity:Activity, collection:String, document:String, subCollection:String, type:Class<T>,   shouldRetry: Boolean, onComplete: suspend (dataList:List<T>)->Unit) {
        
     db.collection(collection).document(document).collection(subCollection)
            .addSnapshotListener(activity) { querySnapShot, error ->
                if (error!=null && shouldRetry){
                    return@addSnapshotListener
                }
                var dataList = emptyList<T>()
                querySnapShot?.let {
                    if (!it.isEmpty){
                        for (doc in it) {
                            if (doc.exists()){
                                val data =  doc.data
                                dataList = dataList.plus(json.fromAny(data, type))
                            }
                        }
                    }
                }

                runBlocking {
                    onComplete(dataList)
                }
            }

    }


     override fun updateUserReview(bookAttr: HashMap<String, FieldValue>?, userReview: UserReview, collection: String, document:String, subCollection: String, subDocument: String): Task<Void> {

         return db.runBatch { batch->
             val reviewDocRef = db.collection(collection).document(document).collection(subCollection).document(subDocument)
             val bookDynamicAttrDocRef = db.collection(collection).document(document)

             val reviewDate = hashMapOf(
                 RemoteDataFields.REVIEW_DATE_TIME.KEY to FieldValue.serverTimestamp()
             )

             batch.set(reviewDocRef, userReview)
             batch.set(reviewDocRef, reviewDate, SetOptions.merge())
             bookAttr?.let {
                 batch.set(bookDynamicAttrDocRef, it, SetOptions.merge())
             }
         }

     }


     override fun updateUserReview(userReviews: List<UserReview>, collection: String, subCollection: String, subDocument: String, bookAttrs: List<HashMap<String, FieldValue>>): Task<Void> {

       return  db.runBatch { batch->
             val length = userReviews.size - 1

             for (i in 0..length){
                 val reviewDocRef = db.collection(collection).document(userReviews[i].isbn).collection(subCollection).document(subDocument)
                 val bookDynamicAttrDocRef = db.collection(collection).document(userReviews[i].isbn)
                 batch.set(reviewDocRef, userReviews[i], SetOptions.merge())
                 batch.set(bookDynamicAttrDocRef, bookAttrs[i], SetOptions.merge())
             }
         }

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

}