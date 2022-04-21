package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.Logger
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@HiltWorker
class UploadUserData  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepo: UserRepo,
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