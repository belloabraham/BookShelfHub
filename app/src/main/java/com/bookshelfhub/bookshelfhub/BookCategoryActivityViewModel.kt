package com.bookshelfhub.bookshelfhub

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.data.Category
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.CartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.PublishedBooksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookCategoryActivityViewModel @Inject constructor(
  @ApplicationContext context: Context,
  val savedState: SavedStateHandle,
  private val publishedBooksRepo: PublishedBooksRepo,
  private  val cartItemsRepo: CartItemsRepo,
  val userAuth: IUserAuth) : ViewModel(){

  private var liveBookByCategory: LiveData<List<PublishedBook>> = MutableLiveData()
  private var flowOfCategory:Flow<PagingData<PublishedBook>>

  private val userId = userAuth.getUserId()

  private val category = savedState.get<String>(Category.TITLE)!!

  private val config  = PagingConfig(
    pageSize = 10,
    prefetchDistance=5,
    enablePlaceholders = true,
    initialLoadSize = 20
  )

  init {

     when (category) {
        context.getString(R.string.trending) -> {
          liveBookByCategory = publishedBooksRepo.getLiveTrendingBooks()
          flowOfCategory = getTrendingBooks()
        }
        context.getString(R.string.recommended_for) -> {
          liveBookByCategory =publishedBooksRepo.getLiveRecommendedBooks()
          flowOfCategory = getRecommendedBooks()
        }
        else -> {
          liveBookByCategory = publishedBooksRepo.getLiveBooksByCategory(category)
          flowOfCategory = getBooksByCategory()
        }
    }

  }

  fun getCategory(): String {
    return category
  }

  fun getFlowOfBookCategory(): Flow<PagingData<PublishedBook>> {
    return flowOfCategory
  }

  fun getLiveTotalCartItemsNo(): LiveData<Int> {
    return cartItemsRepo.getLiveTotalCartItemsNo(userId)
  }

  private fun getTrendingBooks(): Flow<PagingData<PublishedBook>> {
    return  Pager(config){
      publishedBooksRepo.getTrendingBooksPageSource()
    }.flow
  }

  private fun getRecommendedBooks(): Flow<PagingData<PublishedBook>> {
    return  Pager(config){
      publishedBooksRepo.getRecommendedBooksPageSource()
    }.flow
  }

  private fun getBooksByCategory(): Flow<PagingData<PublishedBook>> {
    return  Pager(config){
      publishedBooksRepo.getBooksByCategoryPageSource(category)
    }.flow
  }

  fun getLiveBooksByCategory(): LiveData<List<PublishedBook>> {
    return liveBookByCategory
  }

}