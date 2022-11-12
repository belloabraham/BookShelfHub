package com.bookshelfhub.feature.home.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.data.repos.search_history.ISearchHistoryRepo
import com.bookshelfhub.core.model.entities.StoreSearchHistory
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.core.remote.remote_config.RemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val publishedBooksRepo: IPublishedBooksRepo,
    cartItemsRepo: ICartItemsRepo,
    private val remoteConfig: IRemoteConfig,
    private val  searchHistoryRepo: ISearchHistoryRepo,
    val userAuth: IUserAuth
): ViewModel() {

    private var isBookLoadSuccessfully : MutableLiveData<Boolean> = MutableLiveData()
    private val userId = userAuth.getUserId()
    private var totalCartItems : LiveData<Int> = MutableLiveData()


    private val config  = PagingConfig(
        pageSize = 5,
        enablePlaceholders = true,
        initialLoadSize = 10
    )

    init {
        totalCartItems = cartItemsRepo.getLiveTotalCartItemsNo(userId)
        loadRemotePublishedBooks()
    }

    fun loadRemotePublishedBooks() {
        viewModelScope.launch{
            val totalNoOfLocalPublishedBooks = publishedBooksRepo.getTotalNoOfPublishedBooks()
            val noLocalPublishedBooks = totalNoOfLocalPublishedBooks <= 0
            val thereAreLocalPublishedBooks = !noLocalPublishedBooks

            try {
                if (noLocalPublishedBooks){
                   val  publishedBooks = publishedBooksRepo.getRemotePublishedBooks()
                    publishedBooksRepo.addAllPubBooks(publishedBooks)
                }

                if(thereAreLocalPublishedBooks){
                    val  publishedBooks = publishedBooksRepo.getRemotePublishedBooksFrom(
                      fromSerialNo =   totalNoOfLocalPublishedBooks
                    )
                    publishedBooksRepo.addAllPubBooks(publishedBooks)
                }

                isBookLoadSuccessfully.value = true

            }catch (e:Exception){
                ErrorUtil.e(e)
                isBookLoadSuccessfully.value = false
           }
        }
    }

    fun shouldEnableTrending(): Boolean{
       return  remoteConfig.getBoolean(RemoteConfig.ENABLE_TRENDING)
    }

     suspend fun getPublishedBooksByNameOrAuthor(nameOrAuthor:String): List<PublishedBookUiState>{
        return  publishedBooksRepo.getPublishedBooksByNameOrAuthor(nameOrAuthor)
    }

    suspend fun getTop4StoreSearchHistory(): List<StoreSearchHistory> {
        return searchHistoryRepo.getTop4StoreSearchHistory(userId)
    }

    fun getLiveTotalCartItemsNo():LiveData<Int> {
        return totalCartItems
    }

    fun getDoesBookLoadSuccessfully():LiveData<Boolean> {
        return isBookLoadSuccessfully
    }

    fun getTrendingBooksPageSource(): Flow<PagingData<PublishedBookUiState>> {
        return Pager(config){
            publishedBooksRepo.getTrendingBooksPageSource()
        }.flow
    }

    fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBookUiState>> {
        return Pager(config){
            publishedBooksRepo.getBooksByCategoryPageSource(category)
        }.flow
    }

    fun getRecommendedBooksPageSource(): Flow<PagingData<PublishedBookUiState>> {
        return Pager(config){
            publishedBooksRepo.getRecommendedBooksPageSource()
        }.flow
    }


}