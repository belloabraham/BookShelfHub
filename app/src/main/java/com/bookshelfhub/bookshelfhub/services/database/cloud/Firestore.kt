package com.bookshelfhub.bookshelfhub.services.database.cloud

import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.wrapper.Json
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

open class Firestore {
    private val db:FirebaseFirestore = Firebase.firestore
    private val lastUpdated="last_uploaded"

    private var json:Json?=null
    constructor( json: Json){
        this.json = json
    }

    constructor()

    open fun addDataAsync(data: Any, collection:String, document:String, field:String, lastUpdated:FieldValue = FieldValue.serverTimestamp(), onSuccess: suspend ()->Unit ){
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
            .addOnFailureListener { e ->
            }
    }


   // open fun <T: Any> getDataAsync(collection:String, document: String, field:String, type:Class<T>, onComplete:
     open fun getDataAsync(collection:String, document: String, shouldCache:Boolean=false, onComplete:
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

    open fun <T: Any> getListOfDataAsync(collection:String, field: String, type:Class<T>, shouldCache:Boolean=false,   onComplete: suspend (dataList:List<T>)->Unit){
        db.firestoreSettings=getCacheSettings(shouldCache)
        db.collection(collection)
            .get()
            .addOnSuccessListener { querySnapShot ->
                var dataList = emptyList<T>()
                querySnapShot?.let {
                    if (!it.isEmpty){
                        for (doc in it) {
                            val data =  doc.get(field)
                            dataList = dataList.plus(json!!.fromAny(data!!, type))
                        }
                    }
                }

                runBlocking {
                    onComplete(dataList)
                }
            }
    }


   open fun <T: Any> getLiveListOfDataAsync(collection:String, field: String, type:Class<T>, orderBy:String = DbFields.DATE_TIME_PUBLISHED.KEY, shouldCache:Boolean=false, onComplete: (dataList:List<T>)->Unit){
        db.firestoreSettings=getCacheSettings(shouldCache)
            db.collection(collection)
                .orderBy(orderBy)
            .addSnapshotListener { querySnapShot, e ->

                var dataList = emptyList<T>()
                querySnapShot?.let {

                    if (!it.isEmpty){
                            for (doc in it) {
                                val data =  doc.get(field)
                                dataList = dataList.plus(json!!.fromAny(data!!, type))
                            }
                    }
                }
                onComplete(dataList)
            }
    }

    open fun <T: Any> getLiveListOfDataAsyncFrom(collection:String, field: String, type:Class<T>, startAt:String, orderBy:String = DbFields.DATE_TIME_PUBLISHED.KEY,  shouldCache:Boolean=false, onComplete:  (dataList:List<T>)->Unit){

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
                            dataList = dataList.plus(json!!.fromAny(data!!, type))
                        }
                    }
                }
                onComplete(dataList)
            }
    }



    private fun getCacheSettings(shouldCache: Boolean):FirebaseFirestoreSettings{
     return firestoreSettings {
            isPersistenceEnabled=shouldCache
        }
    }

}