package com.bookshelfhub.bookshelfhub

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
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.IPaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartActivityViewModel @Inject constructor(
  private val cartItemsRepo: ICartItemsRepo,
  private val userRepo: IUserRepo,
  private val paymentTransactionRepo: IPaymentTransactionRepo,
  private val earningsRepo: IEarningsRepo,
  val settingsUtil: SettingsUtil,
  private val userAuth: IUserAuth): ViewModel(){

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
        Timber.e(e)
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

  fun getTotalEarningsInUSD(): Double {
    return totalEarnings
  }

  fun addPaymentTransactions(paymentTransactions:List<PaymentTransaction>){
    viewModelScope.launch {
      paymentTransactionRepo.addPaymentTransactions(paymentTransactions)
    }
  }

  fun getLiveListOfCartItemsAfterEarnings(): LiveData<List<CartItem>> {
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