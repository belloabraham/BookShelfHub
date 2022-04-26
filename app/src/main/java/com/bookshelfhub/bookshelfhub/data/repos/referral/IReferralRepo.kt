package com.bookshelfhub.bookshelfhub.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.google.common.base.Optional

interface IReferralRepo {
    suspend fun addCollaboratorOrIgnore(collaborator: Collaborator)
    fun getALiveOptionalCollaborator(isbn: String): LiveData<Optional<Collaborator>>
     suspend fun getAnOptionalCollaborator(bookId:String): Optional<Collaborator>
}