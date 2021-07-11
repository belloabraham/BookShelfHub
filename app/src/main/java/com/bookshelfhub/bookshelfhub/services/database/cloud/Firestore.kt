package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class Firestore {
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
     open fun getDataAsync(collection:String, document: String, onComplete:
     (data:DocumentSnapshot?, e:Exception?)->Unit){
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

    fun getLiveDataAsync(collection:String, document: String, onComplete:
        (data:DocumentSnapshot?)->Unit){

    }

}