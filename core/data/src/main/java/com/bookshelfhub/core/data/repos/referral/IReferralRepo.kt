package com.bookshelfhub.core.data.repos.referral

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.Collaborator
import com.bookshelfhub.core.model.entities.remote.RemoteCollaborator
import java.util.*

interface IReferralRepo {
    suspend fun addCollaboratorOrReplace(collaborator: Collaborator)
    fun getALiveOptionalCollaborator(isbn: String): LiveData<Optional<Collaborator>>
    suspend fun getAnOptionalCollaborator(bookId:String): Optional<Collaborator>
    suspend fun getARemoteCollaborator(pubId:String, collabId:String, bookId:String):RemoteCollaborator?
}