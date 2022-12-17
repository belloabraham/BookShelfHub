package com.bookshelfhub.core.remote.storage

import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import java.io.File

interface ICloudStorage {

    fun downloadAsTempFile(
        remoteFilePath:String,
        tempLocalFilePath: File,
    ): StorageTask<FileDownloadTask.TaskSnapshot>
}