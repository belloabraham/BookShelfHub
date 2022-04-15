package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.PubReferrers
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.ReferralDao
import com.google.common.base.Optional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReferralRepo @Inject constructor(private val referralDao: ReferralDao) {

    suspend fun addPubReferrer(pubReferrers: PubReferrers){
        withContext(IO){
            referralDao.addPubReferrer(pubReferrers)
        }
    }

    fun getLivePubReferrer(isbn:String): LiveData<Optional<PubReferrers>> {
        return  referralDao.getLivePubReferrer(isbn)
    }
}