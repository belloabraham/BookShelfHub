package com.bookshelfhub.core.data.repos.earnings

import com.bookshelfhub.core.model.Earnings
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EarningsRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    ) : IEarningsRepo {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getRemoteEarnings(userId:String): Earnings? {
       return withContext(ioDispatcher){
           remoteDataSource.getDataAsync(
               RemoteDataFields.USERS_COLL,
               userId,
               RemoteDataFields.EARNINGS,
               userId,
               Earnings::class.java
           )
       }
    }

}