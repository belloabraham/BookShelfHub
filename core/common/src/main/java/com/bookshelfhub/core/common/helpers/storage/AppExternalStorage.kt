package com.bookshelfhub.core.common.helpers.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.content.Intent

object AppExternalStorage {
                                
    fun getDocumentDir(folderPath:String, context:Context):File{
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderPath)
        file.mkdir()
        return file
    }

    fun getDocumentFile(fileDirectory:File, fileNameWithExt:String): File {
        return File(fileDirectory, fileNameWithExt)
    }

    fun getDocumentFile(fileNameWithExt:String, context: Context): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileNameWithExt)
    }

    fun requestClearStorage(applicationContext:Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            applicationContext.startActivity(Intent(ACTION_MANAGE_STORAGE))
        }
    }

}