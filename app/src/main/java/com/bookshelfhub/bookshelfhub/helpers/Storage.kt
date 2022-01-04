package com.bookshelfhub.bookshelfhub.helpers

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.content.Intent


class Storage() {
    
    fun isAudioFileExist(context: Context, fileName:String, fileFormat:String=".mp3", folderName:String = fileName): Boolean {
        val path = "$folderName${File.separator}$fileName$fileFormat"
        val storagePath =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            File(context.getExternalFilesDir(Environment.DIRECTORY_AUDIOBOOKS), path)
        }else{
            File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), path)
        }
        return  storagePath.exists()
    }

    fun isDocumentFileExist(context: Context, fileName:String, fileFormat:String=".pdf", folderName:String = fileName): Boolean {
        val path = "$folderName${File.separator}$fileName$fileFormat"
        val storagePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), path)
        return  storagePath.exists()
    }

    fun getDocumentFilePath(context: Context, folderName: String): String {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName).absolutePath
    }

    fun getAudioFilePath(context: Context, folderName: String): String {
      return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            File(context.getExternalFilesDir(Environment.DIRECTORY_AUDIOBOOKS), folderName).absolutePath
        }else{
            File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), folderName).absolutePath
        }
    }

    fun getFileName(fileName:String, fileFormat:String=".pdf"): String {
        return "$fileName$fileFormat"
    }

    fun requestClearStorage(context:Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val storageIntent = Intent()
            storageIntent.action = ACTION_MANAGE_STORAGE
            context.startActivity(storageIntent)
        }
    }

}