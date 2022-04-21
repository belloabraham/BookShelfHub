package com.bookshelfhub.bookshelfhub.data.repos.paymentcard

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard

interface IPaymentCardRepo {
    suspend fun addPaymentCard(paymentCard: PaymentCard)

    suspend fun deletePaymentCard(card: PaymentCard)

    suspend fun getPaymentCards(): List<PaymentCard>
    fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    suspend fun deleteAllPaymentCards()
}