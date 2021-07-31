package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IEntityId
import com.bookshelfhub.bookshelfhub.wrapper.Json
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


 class Firestore @Inject constructor(val json: Json): ICloudDb {
    private val db:FirebaseFirestore = Firebase.firestore
    private val lastUpdated="last_uploaded"


    override fun addDataAsync(data: Any, collection:String, document:String, field:String, lastUpdated:FieldValue, onSuccess: suspend ()->Unit ){
        val newData = hashMapOf(
            field to data,
            this.lastUpdated to lastUpdated
        )
        db.collection(collection)
            .document(document)
            .set(newData, SetOptions.merge())
            .addOnSuccessListener {
                runBlocking {
                    onSuccess()
                }
            }
    }


     override fun <T: Any> getLiveListOfDataAsync(collection:String, field: String, type:Class<T>, orderBy:String, shouldCache:Boolean, onComplete: (dataList:List<T>)->Unit){
         db.firestoreSettings=getCacheSettings(shouldCache)
         db.collection(collection)
             .orderBy(orderBy)
             .addSnapshotListener { querySnapShot, e ->

                 var dataList = emptyList<T>()
                 querySnapShot?.let {

                     if (!it.isEmpty){
                         for (doc in it) {
                             val data =  doc.get(field)
                             dataList = dataList.plus(json.fromAny(data!!, type))
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


   // open fun <T: Any> getDataAsync(collection:String, document: String, field:String, type:Class<T>, onComplete:
   override fun getDataAsync(collection:String, document: String, shouldCache:Boolean, onComplete:
     (data:DocumentSnapshot?, e:Exception?)->Unit){
       db.firestoreSettings=getCacheSettings(shouldCache)
        db.collection(collection)
            .document(document)
            .get()
            .addOnSuccessListener { documentSnapShot->
                if (documentSnapShot!=null && documentSnapShot.exists()){

                        onComplete(documentSnapShot, null)
                }else{

                        onComplete(null, null)
                }
            }
            .addOnFailureListener {
                    onComplete(null, null)
            }
    }

     override fun <T: Any> getLiveListOfDataAsyncFrom(collection:String, field: String, type:Class<T>, startAt:String, orderBy:String, shouldCache:Boolean, onComplete:  (dataList:List<T>)->Unit){

         db.firestoreSettings=getCacheSettings(shouldCache)
         db.collection(collection)
             .orderBy(orderBy)
             .startAfter(startAt)
             .addSnapshotListener { querySnapShot, e ->

                 var dataList = emptyList<T>()
                 querySnapShot?.let {
                     if (!it.isEmpty){
                         for (doc in it) {
                             val data =  doc.get(field)
                             dataList = dataList.plus(json.fromAny(data!!, type))
                         }
                     }
                 }
                 onComplete(dataList)
             }
     }


     override fun <T: Any> getListOfDataAsync(collection:String, field: String, type:Class<T>, shouldCache:Boolean, onComplete: suspend (dataList:List<T>)->Unit){
         db.firestoreSettings=getCacheSettings(shouldCache)
         db.collection(collection)
             .get()
             .addOnSuccessListener { querySnapShot ->
                 var dataList = emptyList<T>()
                 querySnapShot?.let {
                     if (!it.isEmpty){
                         for (doc in it) {
                             val data =  doc.get(field)
                             dataList = dataList.plus(json.fromAny(data!!, type))
                         }
                     }
                 }

                 runBlocking {
                     onComplete(dataList)
                 }
             }
     }


     //TODO Sub Collections
     override fun <T: Any> getListOfDataAsync(collection:String, document:String, subCollection:String, field: String, type:Class<T>,         shouldCache:Boolean, onComplete: suspend (dataList:List<T>)->Unit){
        db.firestoreSettings=getCacheSettings(shouldCache)
        db.collection(collection).document(document).collection(subCollection)
            .get()
            .addOnSuccessListener { querySnapShot ->
                var dataList = emptyList<T>()
                querySnapShot?.let {
                    if (!it.isEmpty){
                        for (doc in it) {
                            val data =  doc.get(field)
                            dataList = dataList.plus(json.fromAny(data!!, type))
                        }
                    }
                }

                runBlocking {
                    onComplete(dataList)
                }
            }
    }

     override fun addListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, field:String,         lastUpdated: FieldValue, onSuccess: suspend ()->Unit){

        val batch = db.batch()
        for (item in list){
            val docRef = db.collection(collection).document(document).collection(subCollection).document("${item.id}")

            val data = hashMapOf(
                field to item,
                this.lastUpdated to lastUpdated
            )
            batch.set(docRef, data)
        }
        batch.commit().addOnSuccessListener {
            runBlocking {
                onSuccess()
            }
        }
    }

     override fun deleteListOfDataAsync(list: List<IEntityId>, collection: String, document:String, subCollection: String, onSuccess:         suspend ()->Unit){
         val batch = db.batch()
         for (item in list){
             val docRef = db.collection(collection).document(document).collection(subCollection).document("${item.id}")
             batch.delete(docRef)
         }
         batch.commit().addOnSuccessListener {
             runBlocking {
                 onSuccess()
             }
         }
     }



    override fun getCacheSettings(shouldCache: Boolean):FirebaseFirestoreSettings{
     return firestoreSettings {
            isPersistenceEnabled=shouldCache
        }
    }

}