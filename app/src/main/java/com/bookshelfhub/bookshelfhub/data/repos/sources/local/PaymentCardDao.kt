package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.google.common.base.Optional

@Dao
abstract class PaymentCardDao : BaseDao<PaymentCard> {

    @Query("DELETE FROM PaymentCards")
    abstract suspend fun deleteAllPaymentCards()

    @Query("SELECT * FROM PaymentCards")
    abstract fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    @Query("SELECT * FROM PaymentCards")
    abstract suspend fun getPaymentCards(): List<PaymentCard>
}