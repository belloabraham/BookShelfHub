package com.bookshelfhub.core.remote.storage

import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.ktx.storage
import java.io.File

class FirebaseCloudStorage () : ICloudStorage {

    private val  storage = Firebase.storage
    private val storageRef = storage.reference

   override fun downloadAsTempFile(
       remoteFilePath:String,
       tempLocalFilePath: File,
       onProgress:(Int)->Unit,
       onComplete:(downloadTask: Task<FileDownloadTask.TaskSnapshot>)->Unit,
       onError:(Exception)->Unit
    ): StorageTask<FileDownloadTask.TaskSnapshot> {

        val remotePathRef = storageRef.child(remoteFilePath)

       return remotePathRef.getFile(tempLocalFilePath)
            .addOnProgressListener {
                val progress = (it.bytesTransferred/it.totalByteCount)*100
                onProgress(progress.toInt())
            }.addOnCompleteListener{
                onComplete(it)
            }.addOnFailureListener {
                onError(it)
            }
    }
}