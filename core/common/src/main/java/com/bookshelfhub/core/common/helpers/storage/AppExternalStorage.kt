package com.bookshelfhub.core.common.helpers.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.content.Intent

object AppExternalStorage {
    
    fun isAudioFileExist(applicationContext: Context, fileName:String, fileFormat:String=".mp3", folderName:String = fileName): Boolean {
        val path = "$folderName${File.separator}$fileName$fileFormat"
        val storagePath =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_AUDIOBOOKS), path)
        }else{
            File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC), path)
        }
        return  storagePath.exists()
    }

    fun isDocumentFileExist(applicationContext: Context, dirPath:String, fileName:String): Boolean {
        val path = "$dirPath${File.separator}$fileName"
        val storagePath = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), path)
        return  storagePath.exists()
    }

    fun getDocumentFilePath( applicationContext: Context, folderName: String, pathToFile:String): String {
        return File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName+File.separator+pathToFile).absolutePath
    }

    fun getDocumentFilePath(folderName: String, subFolderName:String, fileNameWithExt:String, context: Context): File {
        val path = "$folderName${File.separator}$subFolderName${File.separator}$fileNameWithExt"
        return File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), path)
    }

    fun getAudioFilePath( applicationContext: Context, folderName: String): String {
      return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_AUDIOBOOKS), folderName).absolutePath
        }else{
            File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC), folderName).absolutePath
        }
    }

    fun getFilePath(fileName:String, fileFormat:String=".pdf"): String {
        return "$fileName$fileFormat"
    }

    fun requestClearStorage(applicationContext:Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            applicationContext.startActivity(Intent(ACTION_MANAGE_STORAGE))
        }
    }

}