package com.bookshelfhub.bookshelfhub.data.sources.local

import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction

@Dao
abstract class PaymentTransactionDao : BaseDao<PaymentTransaction> {

    @Query("SELECT * FROM PaymentTransactions")
    abstract suspend fun getAllPaymentTransactions(): List<PaymentTransaction>

    @Query("DELETE FROM PaymentTransactions")
    abstract suspend fun deleteAllPaymentTransactions()

}