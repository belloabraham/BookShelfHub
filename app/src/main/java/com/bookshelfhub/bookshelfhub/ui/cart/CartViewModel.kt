package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.data.models.Earnings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    val remoteDataSource: IRemoteDataSource,
    paymentCardRepo: PaymentCardRepo,
    private val cartItemsRepo: CartItemsRepo, 
     userRepo: UserRepo,
    private val paymentTransactionRepo: PaymentTransactionRepo,
    private val earningsRepo: EarningsRepo,
    val settingsUtil: SettingsUtil,
    userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<CartItem>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()
  private var isNewCardAdded: Boolean = false
  private var flutterEncKey:String?=null
  private var flutterPublicKey:String?=null
  private lateinit var user: User
  private var earnings: MutableLiveData<List<Earnings>> = MutableLiveData()
  private var totalEarnings = 0.0

  init {
    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)
    livePaymentCards = paymentCardRepo.getLivePaymentCards()

    viewModelScope.launch{
      flutterPublicKey = settingsUtil.getString(Settings.FLUTTER_PUBLIC)
      flutterEncKey = settingsUtil.getString(Settings.FLUTTER_ENCRYPTION)

      user =  userRepo.getUser(userId).get()
    }
  }

  fun getTotalEarnings(): Double {
    return totalEarnings
  }

  fun addPaymentTransactions(paymentTransactions:List<PaymentTransaction>){
    viewModelScope.launch {
      paymentTransactionRepo.addPaymentTransactions(paymentTransactions)
    }
  }

  fun getLiveListOfCartItemsAfterEarnings(): LiveData<List<CartItem>> {
    return Transformations.switchMap(getLiveTotalEarnings()) { earnings ->
      for (earning in earnings){
        totalEarnings.plus(earning.earned)
      }
      cartItemsRepo.getLiveListOfCartItems(userId)
    }
  }

  private fun getLiveTotalEarnings(): LiveData<List<Earnings>> {
    viewModelScope.launch {
      try {
        earnings.value = earningsRepo.getRemoteEarnings(userId)
      }catch (e:Exception){
        Timber.e(e)
        return@launch
      }
    }
    return earnings
  }


  fun getUser(): User {
    return user
  }

  fun getFlutterPublicKey(): String? {
    return flutterPublicKey
  }

  fun getFlutterEncKey(): String? {
    return flutterEncKey
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