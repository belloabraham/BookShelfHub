package com.bookshelfhub.book.purchase.ui

import androidx.lifecycle.*
import com.bookshelfhub.core.data.repos.payment_card.IPaymentCardRepo
import com.bookshelfhub.core.model.entities.PaymentCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardInfoViewModel @Inject constructor(
    private val paymentCardRepo: IPaymentCardRepo
): ViewModel(){

    fun addPaymentCard(paymentCard: PaymentCard){
        viewModelScope.launch {
            paymentCardRepo.addPaymentCard(paymentCard)
        }
    }

}