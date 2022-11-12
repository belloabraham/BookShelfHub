package com.bookshelfhub.core.data.repos.bookmarks

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadBookmarks @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookmarksRepo: IBookmarksRepo,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val listOfBookmarks = bookmarksRepo.getLocalBookmarks(isUploaded = false, isDeleted = false)

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
               ErrorUtil.e(e)
                 Result.retry()
             }

    }

}