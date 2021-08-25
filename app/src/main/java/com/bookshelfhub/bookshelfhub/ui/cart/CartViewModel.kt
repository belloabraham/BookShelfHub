package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val localDb: ILocalDb, userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<Cart>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()
  private var isNewCardAdded: Boolean = false

  init {
    liveCartItems = localDb.getLiveListOfCartItems(userId)
    livePaymentCards = localDb.getLivePaymentCards()
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