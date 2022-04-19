package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.BookmarksRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val remoteDataSource: IRemoteDataSource,
    private val bookmarksRepo: BookmarksRepo,
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

        removeAllLocallyDeletedBookmarks()

        //Get bookmarks that was uploaded to the cloud but are now deleted locally
        val listOfPreviouslyUploadedButLaterLocallyDeletedBookmarks  = bookmarksRepo.getDeletedBookmarks(deleted = true, uploaded = true)


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
            Logger.log("Worker:DeleteBookmarks", e)
         Result.retry()
        }

    }

    private suspend fun removeAllLocallyDeletedBookmarks(){
        val listOfLocallyDeletedBookmarks  = bookmarksRepo.getDeletedBookmarks(deleted = true, uploaded = false)
        if (listOfLocallyDeletedBookmarks.isNotEmpty()){
            bookmarksRepo.deleteBookmarks(listOfLocallyDeletedBookmarks)
        }
    }

}