package com.bookshelfhub.downloadmanager.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "downloadmanager.db"
        private const val DATABASE_VERSION = 1
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    AppDbHelper.TABLE_NAME + "( " +
                    DownloadModel.ID + " INTEGER PRIMARY KEY, " +
                    DownloadModel.URL + " VARCHAR, " +
                    DownloadModel.ETAG + " VARCHAR, " +
                    DownloadModel.DIR_PATH + " VARCHAR, " +
                    DownloadModel.FILE_NAME + " VARCHAR, " +
                    DownloadModel.TOTAL_BYTES + " INTEGER, " +
                    DownloadModel.DOWNLOADED_BYTES + " INTEGER, " +
                    DownloadModel.LAST_MODIFIED_AT + " INTEGER " +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, i: Int, i1: Int) {}

}