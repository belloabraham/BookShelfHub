package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.ReferralDao
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReferralRepo @Inject constructor(private val referralDao: ReferralDao) {

    suspend fun addPubReferrer(collaborator: Collaborator){
        withContext(IO){
            referralDao.addPubReferrer(collaborator)
        }
    }

    fun getALiveOptionalCollaborator(isbn:String): LiveData<Optional<Collaborator>> {
        return  referralDao.getLivePubReferrer(isbn)
    }
}