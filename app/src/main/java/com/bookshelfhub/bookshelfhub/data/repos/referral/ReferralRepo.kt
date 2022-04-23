package com.bookshelfhub.bookshelfhub.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.bookshelfhub.bookshelfhub.data.sources.local.ReferralDao
import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReferralRepo @Inject constructor(
    private val referralDao: ReferralDao,
    private val ioDispatcher: CoroutineDispatcher = IO
) :
    IReferralRepo {

    override suspend fun addPubReferrer(collaborator: Collaborator){
        withContext(ioDispatcher){
            referralDao.insertOrReplace(collaborator)
        }
    }

    override fun getALiveOptionalCollaborator(isbn:String): LiveData<Optional<Collaborator>> {
        return  referralDao.getLivePubReferrer(isbn)
    }
}