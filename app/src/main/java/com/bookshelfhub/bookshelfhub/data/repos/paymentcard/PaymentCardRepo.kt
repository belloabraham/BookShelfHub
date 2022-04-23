package com.bookshelfhub.bookshelfhub.data.repos.paymentcard

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.sources.local.PaymentCardDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentCardRepo @Inject constructor(
    private val paymentCardDao: PaymentCardDao,
    private val ioDispatcher: CoroutineDispatcher = IO
) :
    IPaymentCardRepo {

     override suspend fun addPaymentCard(paymentCard: PaymentCard) {
        withContext(ioDispatcher){ paymentCardDao.insertOrReplace(paymentCard)}
    }
     override suspend fun deletePaymentCard(card: PaymentCard) {
         withContext(ioDispatcher){ paymentCardDao.delete(card)}
    }

     override suspend fun getPaymentCards(): List<PaymentCard> {
        return  withContext(ioDispatcher){paymentCardDao.getPaymentCards()}
    }

     override fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
        return paymentCardDao.getLivePaymentCards()
    }

     override suspend fun deleteAllPaymentCards() {
         withContext(ioDispatcher){paymentCardDao.deleteAllPaymentCards()}
    }
}