package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class Firestore {
    private val db:FirebaseFirestore = Firebase.firestore

    open fun addDataAsync(data: Any, collection:String, document:String, onSuccess:()->Unit){
        db.collection(collection)
            .document(document)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { documentReference ->
                onSuccess()
            }
            .addOnFailureListener { e ->
            }

    }

}