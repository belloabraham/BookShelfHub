package com.bookshelfhub.core.data.repos.earnings

import com.bookshelfhub.core.model.Earnings
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import javax.inject.Inject

class EarningsRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    ) : IEarningsRepo {

    override suspend fun getRemoteEarnings(userId:String): Earnings? {
       return remoteDataSource.getDataAsync(
               RemoteDataFields.USERS_COLL,
               userId,
               RemoteDataFields.EARNINGS,
               userId,
               Earnings::class.java
           )
    }

}