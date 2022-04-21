package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard

@Dao
abstract class PaymentCardDao : BaseDao<PaymentCard> {

    @Query("DELETE FROM PaymentCards")
    abstract suspend fun deleteAllPaymentCards()

    @Query("SELECT * FROM PaymentCards")
    abstract fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    @Query("SELECT * FROM PaymentCards")
    abstract suspend fun getPaymentCards(): List<PaymentCard>
}