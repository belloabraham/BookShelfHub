package com.bookshelfhub.bookshelfhub.data.repos.paymentcard

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.sources.local.PaymentCardDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentCardRepo @Inject constructor(private val paymentCardDao: PaymentCardDao) :
    IPaymentCardRepo {

     override suspend fun addPaymentCard(paymentCard: PaymentCard) {
        withContext(IO){ paymentCardDao.insertOrReplace(paymentCard)}
    }
     override suspend fun deletePaymentCard(card: PaymentCard) {
         withContext(IO){ paymentCardDao.delete(card)}
    }

     override suspend fun getPaymentCards(): List<PaymentCard> {
        return  withContext(IO){paymentCardDao.getPaymentCards()}
    }

     override fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
        return paymentCardDao.getLivePaymentCards()
    }

     override suspend fun deleteAllPaymentCards() {
         withContext(IO){paymentCardDao.deleteAllPaymentCards()}
    }
}