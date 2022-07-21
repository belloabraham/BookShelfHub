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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
  val remoteDataSource: IRemoteDataSource,
  paymentCardRepo: IPaymentCardRepo,
  private val cartItemsRepo: ICartItemsRepo,
  userRepo: IUserRepo,
  private val paymentTransactionRepo: IPaymentTransactionRepo,
  private val earningsRepo: IEarningsRepo,
  val settingsUtil: SettingsUtil,
  userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<CartItem>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()
  private var isNewCardAdded: Boolean = false
  private var payStackPublicKey:String?=null
  private lateinit var user: User
  private var earnings: MutableLiveData<Earnings?> = MutableLiveData()
  private var totalEarnings = 0.0

  init {
    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)
    livePaymentCards = paymentCardRepo.getLivePaymentCards()

    viewModelScope.launch{
      payStackPublicKey = settingsUtil.getString(Settings.PAYSTACK_LIVE_PUBLIC_KEY)

      user =  userRepo.getUser(userId).get()
    }
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
    earnings.value = Earnings(0.0)
    viewModelScope.launch {
      try {
        earnings.value = earningsRepo.getRemoteEarnings(userId)
      }catch (e:Exception){
        return@launch
      }
    }
    return earnings
  }


  fun getUser(): User {
    return user
  }

  fun getPayStackLivePublicKey(): String? {
    return payStackPublicKey
  }


  fun setIsNewCard(value:Boolean){
    isNewCardAdded = value
  }

  fun getIsNewCardAdded(): Boolean {
    return isNewCardAdded
  }

  fun getLivePaymentCards(): LiveData<List<PaymentCard>> {
    return livePaymentCards
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