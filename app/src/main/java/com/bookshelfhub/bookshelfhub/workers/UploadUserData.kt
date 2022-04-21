package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepo: IUserRepo,
    private val userAuth:IUserAuth,
): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val userId = userAuth.getUserId()
        val user = userRepo.getUser(userId)

        val userData = user.get()

        if(user.isPresent){
            Result.success()
        }

       return  try {
                userRepo.uploadUser(userData, userId)
                 userData.uploaded = true
                 userRepo.addUser(userData)
                 Result.success()
         }catch (e:Exception){
           Timber.e(e)
             Result.retry()
         }


    }
}