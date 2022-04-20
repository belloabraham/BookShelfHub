package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.BookmarksRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val bookmarksRepo: BookmarksRepo,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val listOfBookmarks = bookmarksRepo.getLocalBookmarks(uploaded = false, deleted = false)

        if(listOfBookmarks.isEmpty()){
            return  Result.success()
        }


               val userId = userAuth.getUserId()

           return  try {
                 bookmarksRepo.updateRemoteUserBookmarks(listOfBookmarks, userId)

                     val length = listOfBookmarks.size-1

                     for (i in 0..length){
                         listOfBookmarks[i].uploaded = true
                     }

                     bookmarksRepo.addBookmarkList(listOfBookmarks)

                     Result.success()

             }catch (e:Exception){
                 Logger.log("Worker:UploadBookmarks", e)
                 Result.retry()
             }

    }

}