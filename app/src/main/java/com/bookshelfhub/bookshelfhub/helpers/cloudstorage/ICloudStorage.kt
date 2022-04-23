package com.bookshelfhub.bookshelfhub.helpers.cloudstorage

import android.net.Uri
import com.google.firebase.storage.FileDownloadTask

interface ICloudStorage {

    suspend fun getDownloadUrl(childFilePath: String): Uri?
    suspend fun downloadFile(
        folder:String,
        subfolder:String,
        fileName:String,
        fileExt:String,
        onProgress:(Long)->Unit,
        onComplete:()->Unit,
        onError:(Exception)->Unit
    ): FileDownloadTask.TaskSnapshot?
}