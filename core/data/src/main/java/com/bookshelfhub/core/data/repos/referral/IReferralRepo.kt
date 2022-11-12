package com.bookshelfhub.core.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.Collaborator
import java.util.*

interface IReferralRepo {
    suspend fun addCollaboratorOrIgnore(collaborator: Collaborator)
    fun getALiveOptionalCollaborator(isbn: String): LiveData<Optional<Collaborator>>
     suspend fun getAnOptionalCollaborator(bookId:String): Optional<Collaborator>
}