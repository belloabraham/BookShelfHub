package com.bookshelfhub.bookshelfhub.data.repos.earnings

import com.bookshelfhub.bookshelfhub.data.models.Earnings
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import javax.inject.Inject

class EarningsRepo @Inject constructor(private val remoteDataSource: IRemoteDataSource) :
    IEarningsRepo {


    override suspend fun getRemoteEarnings(userId:String): List<Earnings> {
       return remoteDataSource.getListOfDataAsync( RemoteDataFields.EARNINGS, RemoteDataFields.REFERRER_ID, userId, Earnings::class.java)
    }

}