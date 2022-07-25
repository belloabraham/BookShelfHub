package com.bookshelfhub.bookshelfhub.ui.main.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val publishedBooksRepo: IPublishedBooksRepo,
    cartItemsRepo: ICartItemsRepo,
    private val remoteConfig: IRemoteConfig,
    private val  searchHistoryRepo: ISearchHistoryRepo,
    val userAuth: IUserAuth): ViewModel() {

    private var isBookLoadSucessfully : MutableLiveData<Boolean> = MutableLiveData()
    private val userId = userAuth.getUserId()
    private var totalCartItems : LiveData<Int> = MutableLiveData()
    private lateinit var booksForSearchFilter:List<PublishedBookUiState>


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

            val noLocalPublishedBooks = totalNoOfLocalPublishedBooks <=0
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
                  //  hello = totalNoOfLocalPublishedBooks
                    publishedBooksRepo.addAllPubBooks(publishedBooks)
                }

                isBookLoadSucessfully.value = true

                booksForSearchFilter = publishedBooksRepo.getListOfPublishedBooksUiState()
            }catch (e:Exception){
                Timber.e(e)
                isBookLoadSucessfully.value = false
           }
        }
    }

    fun shouldEnableTrending(): Boolean{
       return  remoteConfig.getBoolean(com.bookshelfhub.bookshelfhub.data.Config.ENABLE_TRENDING)
    }


    fun getBooksForSearchFiler(): List<PublishedBookUiState> {
        return booksForSearchFilter
    }

    fun getStoreSearchHistory():LiveData<List<StoreSearchHistory>>{
        return searchHistoryRepo.getLiveStoreSearchHistory(userId)
    }

    fun getLiveTotalCartItemsNo():LiveData<Int> {
        return totalCartItems
    }

    fun getDoesBookLoadSuccessfully():LiveData<Boolean> {
        return isBookLoadSucessfully
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