package com.bookshelfhub.bookshelfhub.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.bookshelfhub.bookshelfhub.data.sources.local.ReferralDao
import com.bookshelfhub.bookshelfhub.data.sources.local.RoomInstance
import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReferralRepo @Inject constructor(
    roomInstance: RoomInstance,
) :IReferralRepo {
    private val referralDao = roomInstance.referralDao()
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