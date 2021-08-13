package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import com.google.common.base.Optional
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookItemViewModel @Inject constructor(private val localDb: ILocalDb, val cloudDb:ICloudDb, userAuth: IUserAuth): ViewModel(){

  private var liveCartItems: LiveData<List<Cart>> = MutableLiveData()
  private var liveUserReview: LiveData<Optional<UserReview>> = MutableLiveData()

  val userId = userAuth.getUserId()
  private val config  = PagingConfig(
    pageSize = 5,
    enablePlaceholders = true,
    initialLoadSize = 10
  )

  init {
    liveCartItems = localDb.getLiveListOfCartItems(userId)
     liveUserReview = localDb.getLiveUserReview(userId)
  }

  fun getLiveListOfCartItems(userId:String): LiveData<List<Cart>> {
    return liveCartItems
  }

  fun getLiveUserReview(userId:String): LiveData<Optional<UserReview>> {
    return liveUserReview
  }

  fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBooks>> {
    return Pager(config){
      localDb.getBooksByCategoryPageSource(category)
    }.flow
  }

}