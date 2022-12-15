package com.bookshelfhub.core.data.repos.bookmarks

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.notification.NotificationBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadBookmarks @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookmarksRepo: IBookmarksRepo,
    private val userAuth: IUserAuth
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {

        var notificationId = (4..40).random()
        NotificationBuilder(applicationContext)
            .setTitle("Bookmark worker started")
            .setContentText("Bookmark upload started")
            .Builder(applicationContext)
            .showNotification(notificationId)

        if (!userAuth.getIsUserAuthenticated()){
            notificationId = (4..40).random()
            NotificationBuilder(applicationContext)
                .setTitle("User not authenticated")
                .setContentText("Bookmark upload not started")
                .Builder(applicationContext)
                .showNotification(notificationId)
            return Result.retry()
        }

        val listOfBookmarks = bookmarksRepo.getLocalBookmarks(isUploaded = false, isDeleted = false)

         notificationId = (4..40).random()
        if(listOfBookmarks.isEmpty()){
            NotificationBuilder(applicationContext)
                .setTitle(listOfBookmarks.size.toString())
                .setContentText("Bookmark is empty")
                .Builder(applicationContext)
                .showNotification(notificationId)
            return  Result.success()
        }else{
            NotificationBuilder(applicationContext)
                .setTitle(listOfBookmarks.size.toString())
                .setContentText("Bookmark upload started")
                .Builder(applicationContext)
                .showNotification(notificationId)
        }

           val userId = userAuth.getUserId()

           return  try {
               bookmarksRepo.updateRemoteUserBookmarks(listOfBookmarks, userId)

               for(i in listOfBookmarks.indices){
                  listOfBookmarks[i].uploaded = true
               }

               bookmarksRepo.addBookmarkList(listOfBookmarks)

               Result.success()

             }catch (e:Exception){
               val notificationId2 = (4..40).random()
               NotificationBuilder(applicationContext)
                   .setTitle("Bookmark upload error")
                   .setContentText(e.message.toString())
                   .Builder(applicationContext)
                   .showNotification(notificationId2)
               ErrorUtil.e(e)
               Result.retry()
             }

    }

}