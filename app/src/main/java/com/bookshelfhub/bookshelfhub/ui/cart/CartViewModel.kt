package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.Cart
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.CartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.PaymentCardRepo
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    val remoteDataSource: IRemoteDataSource,
    val cardRepo: PaymentCardRepo,
    paymentCardRepo: PaymentCardRepo,
    private val cartItemsRepo: CartItemsRepo, 
     userRepo: UserRepo,
    val settingsUtil: SettingsUtil,
    userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<Cart>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()
  private var isNewCardAdded: Boolean = false
  private var flutterEncKey:String?=null
  private var flutterPublicKey:String?=null
  private lateinit var user: User

  init {
    liveCartItems = cartItemsRepo.getLiveListOfCartItems(userId)
    livePaymentCards = paymentCardRepo.getLivePaymentCards()

    viewModelScope.launch{
      flutterPublicKey = settingsUtil.getString(Settings.FLUTTER_PUBLIC)
      flutterEncKey = settingsUtil.getString(Settings.FLUTTER_ENCRYPTION)

      user =  userRepo.getUser(userId).get()
    }
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

  fun deleteFromCart(cart: Cart){
    viewModelScope.launch{
      cartItemsRepo.deleteFromCart(cart)
    }
  }

  fun addToCart(cart: Cart){
    viewModelScope.launch {
      cartItemsRepo.addToCart(cart)
    }
  }

  fun getListOfCartItems(): LiveData<List<Cart>> {
    return liveCartItems
  }

}