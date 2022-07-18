package com.bookshelfhub.bookshelfhub.ui.main.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    val connectionUtil: ConnectionUtil,
    private val publishedBooksRepo: IPublishedBooksRepo,
    cartItemsRepo: ICartItemsRepo,
    private val  searchHistoryRepo: ISearchHistoryRepo,
    val userAuth: IUserAuth): ViewModel() {

    private var isBookLoadSucessfully : MutableLiveData<Boolean> = MutableLiveData()
    private val userId = userAuth.getUserId()
    private var totalCartItems : LiveData<Int> = MutableLiveData()
    private lateinit var booksForSearchFiler:List<PublishedBookUiState>


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
                    publishedBooksRepo.addAllPubBooks(publishedBooks)
                }

                isBookLoadSucessfully.value = true
                booksForSearchFiler = publishedBooksRepo.getListOfPublishedBooksUiState()
            }catch (e:Exception){
                Timber.e(e)
                isBookLoadSucessfully.value = false
           }
        }
    }


    fun getBooksForSearchFiler(): List<PublishedBookUiState> {
        return booksForSearchFiler
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