package com.bookshelfhub.core.remote.storage

import android.content.Context
import com.bookshelfhub.core.common.helpers.storage.AppExternalStorage
import com.bookshelfhub.core.common.helpers.storage.FileExtension
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseCloudStorage (
    private val context:Context) : ICloudStorage {

    private val  storage = Firebase.storage
    private var storageRef = storage.reference
    private val SEPERATOR = "/"

   override suspend fun downloadAsTempFile(
       folder:String,
       subfolder:String,
       fileName:String,
       remoteFileExt:String,
       onProgress:(Int)->Unit,
       onComplete:()->Unit,
       onError:(Exception)->Unit
    ): FileDownloadTask.TaskSnapshot? {
        val remotePath = "$folder${SEPERATOR}${subfolder}${SEPERATOR}$fileName$remoteFileExt"
        val remotePathRef = storageRef.child(remotePath)

        val tempLocalFilePath = AppExternalStorage.getDocumentFilePath(
            folder,
            subfolder,
            "$fileName${FileExtension.DOT_TEMP}",
            context)

        return remotePathRef.getFile(tempLocalFilePath)
            .addOnProgressListener {
                val progress = (it.bytesTransferred/it.totalByteCount)*100
                onProgress(progress.toInt())
            }.addOnCompleteListener {
                onComplete()
            }.addOnFailureListener {
                onError(it)
            }.await()
    }
}