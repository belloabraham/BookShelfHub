package com.bookshelfhub.core.data.repos.payment_card

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.PaymentCard

interface IPaymentCardRepo {
    suspend fun addPaymentCard(paymentCard: PaymentCard)

    suspend fun deletePaymentCard(card: PaymentCard)

    suspend fun getPaymentCards(): List<PaymentCard>
    fun getLivePaymentCards(): LiveData<List<PaymentCard>>

    suspend fun deleteAllPaymentCards()
}