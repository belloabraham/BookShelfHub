package com.bookshelfhub.core.remote.storage

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.ktx.storage
import java.io.File

class FirebaseCloudStorage : ICloudStorage {

    private val  storage = Firebase.storage
    private val storageRef = storage.reference

    override fun downloadAsTempFile(
        remoteFilePath:String,
        tempLocalFilePath: File,
    ): StorageTask<FileDownloadTask.TaskSnapshot> {
        val remotePathRef = storageRef.child(remoteFilePath)
        return remotePathRef.getFile(tempLocalFilePath)
    }

}