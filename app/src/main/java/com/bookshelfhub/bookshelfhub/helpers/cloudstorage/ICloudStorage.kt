package com.bookshelfhub.bookshelfhub.helpers.cloudstorage

import android.net.Uri
import com.google.android.gms.tasks.Task
import java.lang.Exception

interface ICloudStorage {
    fun getDownloadUrl(childFilePath: String): Task<Uri>
    fun getDownloadUrl(
        childFilePath: String,
        onSuccess: (Uri) -> Unit,
        onFailure: (error: Exception) -> Unit
    )
}