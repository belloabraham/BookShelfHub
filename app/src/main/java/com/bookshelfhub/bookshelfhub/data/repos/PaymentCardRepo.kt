package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.PaymentCardDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentCardRepo @Inject constructor(private val paymentCardDao: PaymentCardDao) {

     suspend fun addPaymentCard(paymentCard: PaymentCard) {
        withContext(IO){ paymentCardDao.insertOrReplace(paymentCard)}
    }
     suspend fun deletePaymentCard(card: PaymentCard) {
         withContext(IO){ paymentCardDao.delete(card)}
    }

     suspend fun getPaymentCards(): List<PaymentCard> {
        return  withContext(IO){paymentCardDao.getPaymentCards()}
    }

     fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
        return paymentCardDao.getLivePaymentCards()
    }

     suspend fun deleteAllPaymentCards() {
         withContext(IO){paymentCardDao.deleteAllPaymentCards()}
    }
}