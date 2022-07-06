package com.bookshelfhub.bookshelfhub.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.bookshelfhub.bookshelfhub.data.sources.local.AppDatabase
import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReferralRepo @Inject constructor(
    appDatabase: AppDatabase,
) :IReferralRepo {
    private val referralDao = appDatabase.getReferralDao()
    private val ioDispatcher: CoroutineDispatcher = IO

    override suspend fun addCollaboratorOrIgnore(collaborator: Collaborator){
        withContext(ioDispatcher){
            referralDao.insertOrReplace(collaborator)
        }
    }

    override suspend fun getAnOptionalCollaborator(bookId:String): Optional<Collaborator> {
        return withContext(ioDispatcher){ referralDao.getAnOptionalCollaborator(bookId)}
    }

    override fun getALiveOptionalCollaborator(isbn:String): LiveData<Optional<Collaborator>> {
        return  referralDao.getLivePubReferrer(isbn)
    }
}