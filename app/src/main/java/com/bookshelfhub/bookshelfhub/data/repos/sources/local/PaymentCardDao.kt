package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.google.common.base.Optional

@Dao
interface PaymentCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentCard(paymentCard: PaymentCard)

    @Delete
    suspend fun deletePaymentCard(card: PaymentCard)

    @Query("DELETE FROM PaymentCard")
    suspend fun deleteAllPaymentCards()

    @Query("SELECT * FROM PaymentCard")
    fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    @Query("SELECT * FROM PaymentCard")
    suspend fun getPaymentCards(): List<PaymentCard>
}