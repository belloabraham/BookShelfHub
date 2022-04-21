package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.google.common.base.Optional

@Dao
abstract class ReferralDao: BaseDao<Collaborator> {
    @Query("SELECT * FROM Collaborators WHERE bookId = :isbn")
    abstract fun getLivePubReferrer(isbn:String): LiveData<Optional<Collaborator>>

}