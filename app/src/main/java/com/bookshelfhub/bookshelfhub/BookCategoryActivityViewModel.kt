package com.bookshelfhub.bookshelfhub

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.data.Category
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookCategoryActivityViewModel @Inject constructor(
  @ApplicationContext context: Context,
  val savedState: SavedStateHandle,
  private val publishedBooksRepo: IPublishedBooksRepo,
  private  val cartItemsRepo: ICartItemsRepo,
  val userAuth: IUserAuth) : ViewModel(){

  private var booksByCategory: List<PublishedBookUiState> = emptyList()

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

  init {

    viewModelScope.launch {
      val categoryList = when (category) {
          context.getString(R.string.trending) -> {
             publishedBooksRepo.getTrendingBooks()
          }
          context.getString(R.string.recommended_for) -> {
            publishedBooksRepo.getRecommendedBooks()
          }
          else -> {
            publishedBooksRepo.getBooksByCategory(category)
          }
      }

      booksByCategory = booksByCategory.plus(categoryList)

    }

  }

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