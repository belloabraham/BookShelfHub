package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.bookshelfhub.bookshelfhub.data.repos.BookmarksRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@HiltWorker
class DownloadBookmarks @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val bookmarksRepo: BookmarksRepo,
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