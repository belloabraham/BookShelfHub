package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.bookmarks.IBookmarksRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteBookmarks @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookmarksRepo: IBookmarksRepo,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        val userIsNotAuthenticated  = !userAuth.getIsUserAuthenticated()
        if (userIsNotAuthenticated){
            return Result.retry()
        }

        val userId = userAuth.getUserId()

        removeAllLocallyDeletedBookmarksThatWasNotUploadedBefore()

        //Get bookmarks that was uploaded to the cloud but are now deleted locally
        val listOfPreviouslyUploadedButLaterLocallyDeletedBookmarks = bookmarksRepo.getDeletedBookmarks(isDeleted = true, isUploaded = true)

     return  try {
            if (listOfPreviouslyUploadedButLaterLocallyDeletedBookmarks.isNotEmpty()){
                //Delete them from the cloud
                bookmarksRepo.deleteRemoteBookmarks(
                    listOfPreviouslyUploadedButLaterLocallyDeletedBookmarks,
                    userId)
                //Then delete them locally
                bookmarksRepo.deleteBookmarks(listOfPreviouslyUploadedButLaterLocallyDeletedBookmarks)
            }

            Result.success()

        }catch (e:Exception){
         ErrorUtil.e(e)
         Result.retry()
        }

    }

    private suspend fun removeAllLocallyDeletedBookmarksThatWasNotUploadedBefore(){
        val listOfLocallyDeletedBookmarks  = bookmarksRepo.getDeletedBookmarks(isDeleted = true, isUploaded = false)
        if (listOfLocallyDeletedBookmarks.isNotEmpty()){
            bookmarksRepo.deleteBookmarks(listOfLocallyDeletedBookmarks)
        }
    }

}