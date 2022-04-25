package com.bookshelfhub.bookshelfhub.helpers.cloudstorage

import android.net.Uri
import com.google.firebase.storage.FileDownloadTask

interface ICloudStorage {

    suspend fun getDownloadUrl(childFilePath: String): Uri?
    suspend fun downloadAsTempFile(
        folder:String,
        subfolder:String,
        fileName:String,
        remoteFileExt:String,
        onProgress:(Int)->Unit,
        onComplete:()->Unit,
        onError:(Exception)->Unit
    ): FileDownloadTask.TaskSnapshot?
}