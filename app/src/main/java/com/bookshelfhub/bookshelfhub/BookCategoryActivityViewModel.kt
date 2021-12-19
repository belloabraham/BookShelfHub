package com.bookshelfhub.bookshelfhub

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.enums.Category
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookCategoryActivityViewModel @Inject constructor(
  @ApplicationContext context: Context,
  private val localDb: ILocalDb,
  val savedState: SavedStateHandle,
  val userAuth: IUserAuth) : ViewModel(){

  private var liveBookByCategory: LiveData<List<PublishedBook>> = MutableLiveData()
  private var flowOfCategory:Flow<PagingData<PublishedBook>>

  private val userId = userAuth.getUserId()

  private val category = savedState.get<String>(Category.TITLE.KEY)!!

  private val config  = PagingConfig(
    pageSize = 10,
    prefetchDistance=5,
    enablePlaceholders = true,
    initialLoadSize = 20
  )

  init {

     when (category) {
        context.getString(R.string.trending) -> {
          liveBookByCategory = localDb.getLiveTrendingBooks()
          flowOfCategory = getTrendingBooks()
        }
        context.getString(R.string.recommended_for) -> {
          liveBookByCategory =localDb.getLiveRecommendedBooks()
          flowOfCategory = getRecommendedBooks()
        }
        else -> {
          liveBookByCategory = localDb.getLiveBooksByCategory(category)
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
    return localDb.getLiveTotalCartItemsNo(userId)
  }

  private fun getTrendingBooks(): Flow<PagingData<PublishedBook>> {
    return  Pager(config){
      localDb.getTrendingBooksPageSource()
    }.flow
  }

  private fun getRecommendedBooks(): Flow<PagingData<PublishedBook>> {
    return  Pager(config){
      localDb.getRecommendedBooksPageSource()
    }.flow
  }

  private fun getBooksByCategory(): Flow<PagingData<PublishedBook>> {
    return  Pager(config){
      localDb.getBooksByCategoryPageSource(category)
    }.flow
  }

  fun getLiveBooksByCategory(): LiveData<List<PublishedBook>> {
    return liveBookByCategory
  }

}