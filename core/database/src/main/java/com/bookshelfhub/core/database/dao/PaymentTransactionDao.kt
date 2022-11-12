package com.bookshelfhub.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bookshelfhub.core.model.entities.PaymentTransaction

@Dao
abstract class PaymentTransactionDao : BaseDao<PaymentTransaction> {

    @Query("SELECT * FROM PaymentTransactions")
    abstract suspend fun getAllPaymentTransactions(): List<PaymentTransaction>

    @Query("DELETE FROM PaymentTransactions")
    abstract suspend fun deleteAllPaymentTransactions()


    @Query("SELECT * FROM PaymentTransactions WHERE transactionReference = :transactionRef")
    abstract suspend fun getPaymentTransactions(transactionRef:String): List<PaymentTransaction>
}