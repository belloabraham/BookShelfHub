package com.bookshelfhub.bookshelfhub.helpers.cloudstorage

import android.content.Context
import android.net.Uri
import com.bookshelfhub.bookshelfhub.data.FileExtension
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

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

    fun downloadFile(
        folder:String,
        subfolder:String,
        fileName:String,
        fileExt:String,
        onProgress:Long.()->Unit,
        onComplete:()->Unit,
        onError:(error:Exception)->Unit
    ): StorageTask<FileDownloadTask.TaskSnapshot> {
        val remotePath = "$folder${SEPERATOR}${subfolder}${SEPERATOR}$fileName$fileExt"
        val remotePathRef = storageRef.child(remotePath)

        val tempLocalFilePath = AppExternalStorage.getDocumentFilePath(
            folder,
            subfolder,
            "$fileName${FileExtension.DOT_TEMP}",
            applicationContext)

        val localFilePath = AppExternalStorage.getDocumentFilePath(
            folder,
            subfolder,
            fileName+fileExt,
            applicationContext)

     return remotePathRef.getFile(tempLocalFilePath)
            .addOnProgressListener {
                val progress = (it.bytesTransferred/it.totalByteCount)*100
                onProgress(progress-10)
            }
            .addOnSuccessListener {
                try {
                    deleteFile(tempLocalFilePath, localFilePath)
                    onProgress(100)
                    onComplete()
                }catch (e:Exception){
                    onError(e)
                }
            }.addOnFailureListener {
                onError(it)
            }
    }

    @Throws(IOException::class)
    private fun deleteFile(fromFile:File, renameToFile:File){
        try {

            if(renameToFile.exists()){
              val unableToDelete =  !renameToFile.delete()
                if (unableToDelete){
                    throw IOException("Deletion Failed")
                }
            }

            val unableToRename = !fromFile.renameTo(renameToFile)
            if(unableToRename){
                throw IOException("Rename Failed")
            }

        }finally {
            if(fromFile.exists()){
                fromFile.delete()
            }
        }
    }

}