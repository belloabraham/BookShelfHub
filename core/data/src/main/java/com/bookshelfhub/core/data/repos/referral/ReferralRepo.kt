package com.bookshelfhub.core.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.Collaborator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
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