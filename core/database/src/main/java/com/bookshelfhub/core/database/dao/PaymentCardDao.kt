package com.bookshelfhub.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.core.model.entities.PaymentCard

@Dao
abstract class PaymentCardDao : BaseDao<PaymentCard> {

    @Query("DELETE FROM PaymentCards")
    abstract suspend fun deleteAllPaymentCards()

    @Query("SELECT * FROM PaymentCards")
    abstract fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    @Query("SELECT * FROM PaymentCards")
    abstract suspend fun getPaymentCards(): List<PaymentCard>
}