package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.Util
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class DownloadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val cloudDb: ICloudDb,
    private val util: Util,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()

        //Get user bookmarks from the cloud using this path user/userId/bookmarks/id
     val task = cloudDb.getListOfDataAsync(
            DbFields.USERS.KEY,
            userId,
            DbFields.BOOKMARKS.KEY)

      return  if(task.isSuccessful){
          val  bookmarks =  util.queryToListType(task.result, Bookmark::class.java)

          if(bookmarks.isNotEmpty()){
              val length = bookmarks.size-1

              for (i in 0..length){
                  bookmarks[i].uploaded = true
              }
              localDb.addBookmarkList(bookmarks)
          }

             Result.success()

         }else{
             Result.retry()
         }

    }

}