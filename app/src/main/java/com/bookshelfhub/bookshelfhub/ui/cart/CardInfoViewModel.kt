package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.repos.paymentcard.IPaymentCardRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardInfoViewModel @Inject constructor(
    paymentCardRepo: IPaymentCardRepo
): ViewModel(){

  init {

  }


}