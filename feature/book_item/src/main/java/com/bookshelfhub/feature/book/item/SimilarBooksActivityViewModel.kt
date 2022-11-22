package com.bookshelfhub.feature.book.item

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SimilarBooksActivityViewModel @Inject constructor(
  savedState: SavedStateHandle,
  private val publishedBooksRepo: IPublishedBooksRepo,
  private  val cartItemsRepo: ICartItemsRepo,
  val userAuth: IUserAuth
) : ViewModel(){

  private val userId = userAuth.getUserId()

   private val category = savedState.get<String>(SimilarBooks.CATEGORY)!!


  private val config  = PagingConfig(
    pageSize = 10,
    prefetchDistance=5,
    enablePlaceholders = true,
    initialLoadSize = 20
  )

   fun getFlowOfBookBy(): Flow<PagingData<PublishedBookUiState>> {
     return  Pager(config){
       publishedBooksRepo.getBooksByCategoryPageSource(category)
     }.flow
  }

  fun getLiveTotalCartItemsNo(): LiveData<Int> {
    return cartItemsRepo.getLiveTotalCartItemsNo(userId)
  }

  suspend fun getBooksByCategory(): List<PublishedBookUiState> {
    return publishedBooksRepo.getBooksByCategory(category)
  }

}