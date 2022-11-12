package com.bookshelfhub.book.purchase.ui

import androidx.lifecycle.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.earnings.IEarningsRepo
import com.bookshelfhub.core.data.repos.payment_transaction.IPaymentTransactionRepo
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.model.Earnings
import com.bookshelfhub.core.model.entities.CartItem
import com.bookshelfhub.core.model.entities.PaymentTransaction
import com.bookshelfhub.core.model.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartFragmentsViewModel @Inject constructor(
  private val cartItemsRepo: ICartItemsRepo,
  private val userRepo: IUserRepo,
  private val paymentTransactionRepo: IPaymentTransactionRepo,
  private val earningsRepo: IEarningsRepo,
  private val settingsUtil: SettingsUtil,
  private val userAuth: IUserAuth
): ViewModel(){

  private var liveCartItems: LiveData<List<CartItem>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var payStackPublicKey:String?=null
  private var earnings: MutableLiveData<Earnings?> = MutableLiveData()
  private var totalEarnings = 0.0
  private var totalAmountInUSD = 0.0
  private var combinedBookIds = ""
  private var paymentTransactions = emptyList<PaymentTransaction>()

  init {
    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)

    viewModelScope.launch{
      payStackPublicKey = settingsUtil.getString(Settings.PAYSTACK_LIVE_PUBLIC_KEY)
    }

    viewModelScope.launch {
      try {
        earnings.value = earningsRepo.getRemoteEarnings(userId)
      }catch (e:Exception){
        ErrorUtil.e(e)
        earnings.value = Earnings(0.0)
      }
    }
  }

  fun getPaymentTransactions(): List<PaymentTransaction> {
    return paymentTransactions
  }

  fun setPaymentTransactions(value: List<PaymentTransaction>){
    paymentTransactions = value
  }

  fun getCombinedBookIds(): String {
    return combinedBookIds
  }

  fun setCombinedBookIds(value:String){
    combinedBookIds = value
  }

  fun getUserId(): String {
    return userId
  }

  fun getTotalAmountInUSD(): Double {
    return totalAmountInUSD
  }

  fun setTotalAmountInUSD(value:Double){
    totalAmountInUSD   = value
  }

  fun getTotalEarningsInLocalCurrency(): Double {
    return totalEarnings
  }

  fun addPaymentTransactions(paymentTransactions:List<PaymentTransaction>){
    viewModelScope.launch {
      paymentTransactionRepo.addPaymentTransactions(paymentTransactions)
    }
  }

  fun getListOfCartItemsAfterEarnings(): LiveData<List<CartItem>> {
    return Transformations.switchMap(getLiveTotalEarnings()) { earnings ->
      totalEarnings = earnings?.total ?: 0.0
      cartItemsRepo.getLiveListOfCartItems(userId)
    }
  }

  private fun getLiveTotalEarnings(): LiveData<Earnings?> {
    return earnings
  }

  fun getUserEmail(): String? {
    return userAuth.getEmail()
  }

  suspend fun getUser(): User {
    return userRepo.getUser(userId).get()
  }

  fun getPayStackLivePublicKey(): String? {
    return payStackPublicKey
  }


  fun deleteFromCart(cart: CartItem){
    viewModelScope.launch{
      cartItemsRepo.deleteFromCart(cart)
    }
  }

  fun addToCart(cart: CartItem){
    viewModelScope.launch {
      cartItemsRepo.addToCart(cart)
    }
  }

  fun getListOfCartItems(): LiveData<List<CartItem>> {
    return liveCartItems
  }

}