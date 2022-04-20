package com.bookshelfhub.bookshelfhub.data.repos

import com.bookshelfhub.bookshelfhub.data.models.Earnings
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import javax.inject.Inject

class EarningsRepo @Inject constructor(private val remoteDataSource: IRemoteDataSource) {


    suspend fun getRemoteEarnings(userId:String): List<Earnings> {
       return remoteDataSource.getListOfDataAsync( RemoteDataFields.EARNINGS, RemoteDataFields.REFERRER_ID, userId, Earnings::class.java)
    }

}