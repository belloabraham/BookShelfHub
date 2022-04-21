package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.IBookmarksRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DownloadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val bookmarksRepo: IBookmarksRepo,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
workerParams
){
    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()

         return  try {
                val bookmarks = bookmarksRepo.getRemoteBookmarks(userId)

                if (bookmarks.isNotEmpty()) {
                    val length = bookmarks.size - 1

                    for (i in 0..length) {
                        bookmarks[i].uploaded = true
                    }
                    bookmarksRepo.addBookmarkList(bookmarks)
                }

                Result.success()

            } catch (e: Exception) {
               Timber.e(e)
               Result.retry()
            }

        }

}