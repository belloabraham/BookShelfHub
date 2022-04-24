package com.bookshelfhub.bookshelfhub.helpers.cloudstorage

import android.content.Context
import android.net.Uri
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseCloudStorage (
    private val applicationContext:Context,
    private val ioDispatcher: CoroutineDispatcher = IO) : ICloudStorage {

    private val  storage = Firebase.storage
    private var storageRef = storage.reference
    private val SEPERATOR = "/"

    override suspend fun getDownloadUrl(childFilePath:String): Uri? {
        return withContext(ioDispatcher){
            storageRef.child(childFilePath).downloadUrl.await()
        }
    }

   override suspend fun downloadAsTempFile(
       folder:String,
       subfolder:String,
       fileName:String,
       remoteFileExt:String,
       onProgress:(Long)->Unit,
       onComplete:()->Unit,
       onError:(Exception)->Unit
    ): FileDownloadTask.TaskSnapshot? {
        val remotePath = "$folder${SEPERATOR}${subfolder}${SEPERATOR}$fileName$remoteFileExt"
        val remotePathRef = storageRef.child(remotePath)

        val tempLocalFilePath = AppExternalStorage.getDocumentFilePath(
            folder,
            subfolder,
            "$fileName${FileExtension.DOT_TEMP}",
            applicationContext)

     return remotePathRef.getFile(tempLocalFilePath)
            .addOnProgressListener {
                val progress = (it.bytesTransferred/it.totalByteCount)*100
                onProgress(progress-10)
            }
            .addOnSuccessListener {
                try {
                    onComplete()
                }catch (e:Exception){
                    onError(e)
                }
            }.addOnFailureListener {
                onError(it)
            }.await()
    }


}