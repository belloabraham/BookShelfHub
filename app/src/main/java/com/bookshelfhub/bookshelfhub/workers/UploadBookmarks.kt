package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb, private val remoteDataSource: IRemoteDataSource, private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val listOfBookmarks = localDb.getLocalBookmarks(uploaded = false, deleted = false)

     return   if (listOfBookmarks.isNotEmpty()){
             val userId = userAuth.getUserId()

             try {
                 val  task =  remoteDataSource.addListOfDataAsync(listOfBookmarks, RemoteDataFields.USERS_COLL, userId,  RemoteDataFields.BOOKMARKS_COLL).await()

                 task.run {
                     val length = listOfBookmarks.size-1

                     for (i in 0..length){
                         listOfBookmarks[i].uploaded = true
                     }

                     localDb.addBookmarkList(listOfBookmarks)

                     Result.success()
                 }

             }catch (e:Exception){
                 Logger.log("Worker:UploadBookmarks", e)
                 Result.retry()
             }

        }else{
            Result.success()
        }

    }

}