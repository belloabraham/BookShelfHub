package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.Collaborator
import com.google.common.base.Optional

@Dao
interface ReferralDao {
    @Query("SELECT * FROM PubReferrers WHERE isbn = :isbn")
    fun getLivePubReferrer(isbn:String): LiveData<Optional<Collaborator>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPubReferrer(collaborator: Collaborator)
}