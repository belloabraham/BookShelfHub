package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.google.common.base.Optional

@Dao
abstract class PaymentTransactionDao : BaseDao<PaymentTransaction> {

    @Query("SELECT * FROM PaymentTransaction")
    abstract suspend fun getAllPaymentTransactions(): List<PaymentTransaction>

    @Query("DELETE FROM PaymentTransaction")
    abstract suspend fun deleteAllPaymentTransactions()

}