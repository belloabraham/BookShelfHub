package com.bookshelfhub.bookshelfhub.services.database.cloud

import androidx.paging.PagingSource
import com.bookshelfhub.bookshelfhub.wrapper.Json
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

open class Firestore (private val json: Json) {
    private val db:FirebaseFirestore = Firebase.firestore
    private val lastUpdated="last_uploaded"

    open fun addDataAsync(data: Any, collection:String, document:String, field:String, lastUpdated:FieldValue = FieldValue.serverTimestamp(), onSuccess:()->Unit ){
        val newData = hashMapOf(
            field to data,
            this.lastUpdated to lastUpdated
        )
        db.collection(collection)
            .document(document)
            .set(newData, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
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
                onComplete(null, it)
            }
    }

   open fun <T: Any> getLiveListOfDataAsync(collection:String, field: String, shouldCache:Boolean=false, type:Class<T>, onComplete: (dataList:List<T>?)->Unit){
        db.firestoreSettings=getCacheSettings(shouldCache)
            db.collection(collection)
            .addSnapshotListener { querySnapShot, e ->
                var dataList = emptyList<T>()

                querySnapShot?.let {
                    try {
                        for (doc in it) {
                            val data =  doc.get(field)
                            dataList = dataList.plus(json.fromAny(data!!, type))
                        }
                    }catch(e:Exception){
                    }
                }
                onComplete(dataList)
            }
    }

    private fun getCacheSettings(shouldCache: Boolean):FirebaseFirestoreSettings{
     return firestoreSettings {
            isPersistenceEnabled=false
        }
    }

}