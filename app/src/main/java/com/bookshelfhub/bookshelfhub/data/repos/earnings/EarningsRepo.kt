package com.bookshelfhub.bookshelfhub.data.repos.earnings

import com.bookshelfhub.bookshelfhub.data.models.Earnings
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EarningsRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) :
    IEarningsRepo {


    override suspend fun getRemoteEarnings(userId:String): List<Earnings> {
       return withContext(ioDispatcher){
           remoteDataSource.getListOfDataAsync(
               RemoteDataFields.EARNINGS,
               RemoteDataFields.REFERRER_ID,
               userId, Earnings::class.java)
       }
    }

}