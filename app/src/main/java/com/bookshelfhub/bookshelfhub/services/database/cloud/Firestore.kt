package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

open class Firestore {
    private val db:FirebaseFirestore = Firebase.firestore

    open fun addDataAsync(data: Any, collection:String, document:String, field:String, onSuccess:()->Unit){

        val newData = hashMapOf(
            field to data
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
            .addOnSuccessListener {
                if (it!=null && it.exists()){
                   onComplete(it.toObject<User>())
                    //onComplete(null)
                }else{
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }

    }

}