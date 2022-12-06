package com.bookshelfhub.core.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.Collaborator
import com.bookshelfhub.core.model.entities.remote.RemoteCollaborator
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.RemoteDataFields
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ReferralRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val remoteDataSource: IRemoteDataSource,
    ) :IReferralRepo {
    private val referralDao = appDatabase.getReferralDao()
    private val ioDispatcher: CoroutineDispatcher = IO

    override suspend fun addCollaboratorOrReplace(collaborator: Collaborator){
        withContext(ioDispatcher){
            referralDao.insertOrReplace(collaborator)
        }
    }

    override suspend fun getARemoteCollaborator(pubId:String, collabId:String, bookId:String):RemoteCollaborator?{
        val collabAndBookId = "${collabId}-${bookId}"
        return withContext(ioDispatcher){
            remoteDataSource.getDataAsync(
                RemoteDataFields.PUBLISHERS,
                pubId,
                RemoteDataFields.COLLABORATORS_COLL,
                collabAndBookId,
                RemoteCollaborator::class.java
            )
        }
    }

    override suspend fun getAnOptionalCollaborator(bookId:String): Optional<Collaborator> {
        return withContext(ioDispatcher){ referralDao.getAnOptionalCollaborator(bookId)}
    }

    override fun getALiveOptionalCollaborator(isbn:String): LiveData<Optional<Collaborator>> {
        return  referralDao.getLivePubReferrer(isbn)
    }
}