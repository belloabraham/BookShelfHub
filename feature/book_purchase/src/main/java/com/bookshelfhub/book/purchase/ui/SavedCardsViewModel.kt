package com.bookshelfhub.book.purchase.ui

import androidx.lifecycle.*
import com.bookshelfhub.core.data.repos.payment_card.IPaymentCardRepo
import com.bookshelfhub.core.model.entities.PaymentCard
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedCardsViewModel @Inject constructor(
  paymentCardRepo: IPaymentCardRepo
): ViewModel(){

  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()

  init {
    livePaymentCards = paymentCardRepo.getLivePaymentCards()
  }

  fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
    return livePaymentCards
  }

}