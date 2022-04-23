package com.bookshelfhub.bookshelfhub.helpers.cloudstorage

import android.net.Uri

interface ICloudStorage {

    suspend fun getDownloadUrl(childFilePath: String): Uri?

}