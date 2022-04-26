package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
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
    private lateinit var publishedBooks:List<PublishedBook>
    private lateinit var booksForSearchFiler:List<PublishedBook>


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
            publishedBooks = publishedBooksRepo.getPublishedBooks()

            try {
                if (publishedBooks.isEmpty()){
                     publishedBooks = publishedBooksRepo.getRemotePublishedBooks()
                    publishedBooksRepo.addAllPubBooks(publishedBooks)
                }

                if(publishedBooks.isNotEmpty()){
                    val fromBookSerialNo = publishedBooks.size
                     publishedBooks = publishedBooksRepo.getRemotePublishedBooksFrom(fromBookSerialNo)
                    publishedBooksRepo.addAllPubBooks(publishedBooks)
                }

                isBookLoadSucessfully.value = true
                booksForSearchFiler = publishedBooksRepo.getPublishedBooks()
            }catch (e:Exception){
                Timber.e(e)
                isBookLoadSucessfully.value = false
           }
        }
    }


    fun getBooksForSearchFiler(): List<PublishedBook> {
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

    fun getTrendingBooksPageSource(): Flow<PagingData<PublishedBook>> {
        return Pager(config){
            publishedBooksRepo.getTrendingBooksPageSource()
        }.flow
    }

    fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBook>> {
        return Pager(config){
            publishedBooksRepo.getBooksByCategoryPageSource(category)
        }.flow
    }

    fun getRecommendedBooksPageSource(): Flow<PagingData<PublishedBook>> {
        return Pager(config){
            publishedBooksRepo.getRecommendedBooksPageSource()
        }.flow
    }


}