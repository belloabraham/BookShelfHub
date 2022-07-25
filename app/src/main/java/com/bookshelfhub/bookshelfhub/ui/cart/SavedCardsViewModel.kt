package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.models.Earnings
import com.bookshelfhub.bookshelfhub.helpers.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.earnings.IEarningsRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymentcard.IPaymentCardRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.IPaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedCardsViewModel @Inject constructor(
  val remoteDataSource: IRemoteDataSource,
  paymentCardRepo: IPaymentCardRepo,): ViewModel(){

  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()

  init {
    livePaymentCards = paymentCardRepo.getLivePaymentCards()
  }

  fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
    return livePaymentCards
  }

}