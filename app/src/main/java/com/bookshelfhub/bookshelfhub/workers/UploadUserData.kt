package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.enums.DbCollections
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import kotlinx.coroutines.runBlocking

class UploadUserData(var context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams
) {

    private lateinit var localDb: LocalDb
    private lateinit var cloudDb: CloudDb
    private lateinit var userAuth: UserAuth


    override fun doWork(): Result {

        localDb = LocalDb(context)
        val listOfUsersData = localDb.getUsers().value?.filter {
            it.isUploaded==false
        }
        val userData = listOfUsersData?.get(0)
        userData?.let {
            cloudDb = CloudDb()
            userAuth=UserAuth()
            cloudDb.addDataAsync(it, DbCollections.USERS.KEY, userAuth.getUserId()){
                it.isUploaded=true
                runBlocking {
                    localDb.addUser(it)
                }
            }
        }
        return Result.success()
    }
}