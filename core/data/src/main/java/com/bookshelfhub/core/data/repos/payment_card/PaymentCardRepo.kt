package com.bookshelfhub.core.data.repos.payment_card

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.PaymentCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentCardRepo @Inject constructor(
    appDatabase: AppDatabase,
) : IPaymentCardRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val paymentCardDao = appDatabase.getPaymentCardDao()

     override suspend fun addPaymentCard(paymentCard: PaymentCard) {
        withContext(ioDispatcher){
            paymentCardDao.insertOrReplace(paymentCard)
        }
    }
     override suspend fun deletePaymentCard(card: PaymentCard) {
        return withContext(ioDispatcher){
             paymentCardDao.delete(card)
         }
    }

     override suspend fun getPaymentCards(): List<PaymentCard> {
        return  withContext(ioDispatcher){
            paymentCardDao.getPaymentCards()
        }
    }

     override fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
        return paymentCardDao.getLivePaymentCards()
    }

     override suspend fun deleteAllPaymentCards() {
         withContext(ioDispatcher){
             paymentCardDao.deleteAllPaymentCards()
         }
    }
}