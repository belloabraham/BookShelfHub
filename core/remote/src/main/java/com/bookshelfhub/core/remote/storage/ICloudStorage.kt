package com.bookshelfhub.core.remote.storage

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import java.io.File

interface ICloudStorage {

     fun downloadAsTempFile(
         remoteFilePath:String,
         tempLocalFilePath: File,
         onProgress:(Int)->Unit,
         onComplete:(downloadTask: Task<FileDownloadTask.TaskSnapshot>)->Unit,
         onError:(Exception)->Unit
    ): StorageTask<FileDownloadTask.TaskSnapshot>
}