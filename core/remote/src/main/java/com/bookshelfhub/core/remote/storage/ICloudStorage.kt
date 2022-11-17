package com.bookshelfhub.core.remote.storage

import android.net.Uri
import com.google.firebase.storage.FileDownloadTask

interface ICloudStorage {

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