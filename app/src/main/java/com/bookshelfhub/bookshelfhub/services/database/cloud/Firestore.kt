package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
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


    open fun <T: Any> getDataAsync(collection:String, document: String, field:String, type:Class<T>, onComplete:(data:Any?)->Unit){
        db.collection(collection)
            .document(document)
            .get()
            .addOnSuccessListener { documentSnapShot->
                if (documentSnapShot!=null && documentSnapShot.exists()){
                    try {
                        documentSnapShot.get(field, type)?.let{
                            onComplete(it)
                        }
                    }catch (e:Exception){
                        onComplete(null)
                    }
                }else{
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

}