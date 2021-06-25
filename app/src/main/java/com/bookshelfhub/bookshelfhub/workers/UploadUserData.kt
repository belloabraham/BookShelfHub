package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.models.User
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import kotlinx.coroutines.runBlocking

class UploadUserData(var context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams
) {

    private lateinit var localDb: LocalDb
    private lateinit var cloudDb: CloudDb
    private lateinit var userAuth: UserAuth
    private lateinit var stringUtil: StringUtil

    override fun doWork(): Result {
        localDb = LocalDb(context)
        val user = localDb.getUser()
        val userData = user.get()
            if (user.isPresent && !userData.uploaded){
                stringUtil = StringUtil()
                cloudDb = CloudDb()
                userAuth=UserAuth(stringUtil)
                    cloudDb.addDataAsync(userData, DbFields.USERS_COLL.KEY, userAuth.getUserId(), DbFields.USER.KEY){
                        val newUserData = userData.copy(uploaded = true)
                        runBlocking {
                            localDb.addUser(newUserData)
                        }
                    }
            }
        return Result.success()
    }
}