package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.Collaborator
import java.util.*

@Dao
abstract class ReferralDao: BaseDao<Collaborator> {

    @Query("SELECT * FROM Collaborators WHERE bookId = :bookId")
    abstract fun getLivePubReferrer(bookId:String): LiveData<Optional<Collaborator>>

    @Query("SELECT * FROM Collaborators WHERE bookId = :bookId")
    abstract suspend fun getAnOptionalCollaborator(bookId:String): Optional<Collaborator>

}