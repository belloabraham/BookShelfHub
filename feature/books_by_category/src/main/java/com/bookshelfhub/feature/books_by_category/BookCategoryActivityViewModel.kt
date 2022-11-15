package com.bookshelfhub.feature.books_by_category

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookCategoryActivityViewModel @Inject constructor(
  @ApplicationContext context: Context,
  savedState: SavedStateHandle,
  private val publishedBooksRepo: IPublishedBooksRepo,
  private  val cartItemsRepo: ICartItemsRepo,
  val userAuth: IUserAuth
) : ViewModel(){

  private val userId = userAuth.getUserId()
  private val trendingCategory = context.getString(R.string.trending)
  private val recommendedCategory =  context.getString(R.string.recommended_for)
  private val category = savedState.get<String>(Category.TITLE)!!


  private val config  = PagingConfig(
    pageSize = 10,
    prefetchDistance=5,
    enablePlaceholders = true,
    initialLoadSize = 20
  )

  fun getCategory(): String {
    return category
  }

   fun getFlowOfBookCategory(): Flow<PagingData<PublishedBookUiState>> {
    return when (category) {
      trendingCategory -> {
         getFlowOfTrendingBooks()
      }
      recommendedCategory -> {
        getFowOfRecommendedBooks()
      }
      else -> {
        getFlowOfBooksByCategory()
      }
    }
  }

  fun getLiveTotalCartItemsNo(): LiveData<Int> {
    return cartItemsRepo.getLiveTotalCartItemsNo(userId)
  }

  private fun getFlowOfTrendingBooks(): Flow<PagingData<PublishedBookUiState>> {
    return  Pager(config){
      publishedBooksRepo.getTrendingBooksPageSource()
    }.flow
  }

  private fun getFowOfRecommendedBooks(): Flow<PagingData<PublishedBookUiState>> {
    return  Pager(config){
      publishedBooksRepo.getRecommendedBooksPageSource()
    }.flow
  }

  private fun getFlowOfBooksByCategory(): Flow<PagingData<PublishedBookUiState>> {
    return  Pager(config){
      publishedBooksRepo.getBooksByCategoryPageSource(category)
    }.flow
  }

  suspend fun getBooksByCategory(): List<PublishedBookUiState> {
    return when (category) {
       trendingCategory -> {
         publishedBooksRepo.getTrendingBooks()
       }
       recommendedCategory -> {
         publishedBooksRepo.getRecommendedBooks()
       }
       else -> {
         publishedBooksRepo.getBooksByCategory(category)
       }
     }
  }

}