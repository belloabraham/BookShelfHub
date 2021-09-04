package com.bookshelfhub.bookshelfhub.ui.cart

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.models.Earnings
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val localDb: ILocalDb, val cloudDb: ICloudDb, userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<Cart>> = MutableLiveData()
  private val userId = userAuth.getUserId()
  private var livePaymentCards: LiveData<List<PaymentCard>> = MutableLiveData()
  private var earnings: MutableLiveData<List<Earnings>> = MutableLiveData()
  private var isNewCardAdded: Boolean = false

  init {
    liveCartItems = localDb.getLiveListOfCartItems(userId)
    livePaymentCards = localDb.getLivePaymentCards()

    cloudDb.getListOfDataAsync(DbFields.EARNINGS.KEY, DbFields.REFERRER_ID.KEY, userId, Earnings::class.java, shouldRetry = true){

      earnings.value = it

    }

  }


  fun getUserEarnings(): LiveData<List<Earnings>> {
    return earnings
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