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
class CardInfoViewModel @Inject constructor(
    paymentCardRepo: PaymentCardRepo): ViewModel(){

  init {

  }


}