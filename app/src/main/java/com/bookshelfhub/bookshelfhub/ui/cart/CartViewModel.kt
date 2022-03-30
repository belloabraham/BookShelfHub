package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.domain.models.entities.Cart
import com.bookshelfhub.bookshelfhub.domain.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.domain.models.entities.User
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.domain.data.repos.sources.remote.ICloudDb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val localDb: ILocalDb,
    val cloudDb: ICloudDb,
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
    liveCartItems = localDb.getLiveListOfCartItems(userId)
    livePaymentCards = localDb.getLivePaymentCards()

    viewModelScope.launch(IO){
      flutterPublicKey = settingsUtil.getString(Settings.FLUTTER_PUBLIC.KEY)
      flutterEncKey = settingsUtil.getString(Settings.FLUTTER_ENCRYPTION.KEY)

      user =  localDb.getUser(userId).get()
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
    viewModelScope.launch(IO){
      localDb.deleteFromCart(cart)
    }
  }

  fun addToCart(cart: Cart){
    viewModelScope.launch(IO) {
      localDb.addToCart(cart)
    }
  }

  fun getListOfCartItems(): LiveData<List<Cart>> {
    return liveCartItems
  }

}