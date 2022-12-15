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
                    for (i in bookmarks.indices) {
                        bookmarks[i].uploaded = true
                    }
                    bookmarksRepo.addBookmarkList(bookmarks)
                }

                Result.success()

            } catch (e: Exception) {
               ErrorUtil.e(e)
               Result.retry()
            }

        }

}