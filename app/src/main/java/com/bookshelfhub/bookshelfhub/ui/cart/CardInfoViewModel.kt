package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.repos.paymentcard.IPaymentCardRepo
import com.bookshelfhub.bookshelfhub.workers.Worker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardInfoViewModel @Inject constructor(
    private val paymentCardRepo: IPaymentCardRepo
): ViewModel(){

    fun addPaymentCard(paymentCard:PaymentCard){
        viewModelScope.launch {
            paymentCardRepo.addPaymentCard(paymentCard)
        }
    }

}