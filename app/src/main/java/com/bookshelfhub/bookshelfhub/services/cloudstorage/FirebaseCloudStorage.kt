package com.bookshelfhub.bookshelfhub.services.cloudstorage

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class FirebaseCloudStorage : ICloudStorage {

    private val  storage = Firebase.storage
    private var storageRef = storage.reference

    override fun getDownloadUrl(childFilePath:String): Task<Uri> {
        return storageRef.child(childFilePath).downloadUrl
    }

    override fun getDownloadUrl(childFilePath:String, onSuccess:(Uri)->Unit, onFailure:(error:Exception)->Unit) {
         storageRef.child(childFilePath).downloadUrl.addOnSuccessListener {
            onSuccess(it)
        }.addOnFailureListener {
             onFailure(it)
        }
    }
}