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
import com.bookshelfhub.payment.PaymentSDKType
import com.bookshelfhub.payment.SupportedCurrencies
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
  userAuth: IUserAuth
): ViewModel(){

  private var liveCartItems: LiveData<List<CartItem>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var payStackPublicKey:String?=null
  private var userEarningsFromReferrals: MutableLiveData<Earnings?> = MutableLiveData()
  private var totalEarnings = 0.0
  internal var subtractedUserEarnings = 0.0
  internal var combinedBookIds = ""
  internal var totalCostOfBook:Double=0.0
  internal var paymentSDKType: PaymentSDKType? = null
  internal var currencyToChargeForBooksSale = SupportedCurrencies.NGN
  private var paymentTransactions = mutableListOf<PaymentTransaction>()

  init {

    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)

    viewModelScope.launch{
      payStackPublicKey = settingsUtil.getString(Settings.PAYSTACK_LIVE_PUBLIC_KEY)
    }

    viewModelScope.launch {
      try {
        userEarningsFromReferrals.value = earningsRepo.getRemoteEarnings(userId)
      }catch (e:Exception){
        ErrorUtil.e(e)
        userEarningsFromReferrals.value = Earnings(0.0)
      }
    }
  }

  fun getPaymentTransactions(): MutableList<PaymentTransaction> {
    return paymentTransactions
  }

  fun getUserId(): String {
    return userId
  }

  fun getTotalUserEarnings(): Double {
    return totalEarnings
  }

  fun initializePaymentVerificationProcess(paymentTransactions:List<PaymentTransaction>){
    viewModelScope.launch {

      paymentTransactionRepo.initializePaymentVerificationProcess(
        paymentTransactions,
        currencyToChargeForBooksSale,
        paymentSDKType!!,
        subtractedUserEarnings
      )
    }
  }

  fun getListOfCartItemsAfterUserEarningsFromReferrals(): LiveData<List<CartItem>> {
    return Transformations.switchMap(getLiveTotalEarnings()) { earnings ->
      totalEarnings = earnings?.total ?: 0.0
      getLiveListOfCartItems()
    }
  }

  private fun getLiveTotalEarnings(): LiveData<Earnings?> {
    return userEarningsFromReferrals
  }

  suspend fun getUserEmail(): String{
    return userRepo.getUser(userId).get().email
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

  private fun getLiveListOfCartItems(): LiveData<List<CartItem>> {
    return liveCartItems
  }

}