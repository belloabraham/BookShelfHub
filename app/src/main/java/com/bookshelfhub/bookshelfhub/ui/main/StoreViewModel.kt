package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.repos.CartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.PublishedBooksRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    val connectionUtil: ConnectionUtil,
    private val publishedBooksRepo: PublishedBooksRepo,
    cartItemsRepo: CartItemsRepo,
    private val remoteDataSource: IRemoteDataSource,
    val userAuth: IUserAuth): ViewModel() {

    private var allPublishedBook : LiveData<List<PublishedBook>> = MutableLiveData()
    private var isNoConnection : MutableLiveData<Boolean> = MutableLiveData()
    private var isNetworkError : MutableLiveData<Boolean> = MutableLiveData()
    private val userId = userAuth.getUserId()
    private var totalCartItems : LiveData<Int> = MutableLiveData()
    private lateinit var publishedBooks:List<PublishedBook>


    private val config  = PagingConfig(
        pageSize = 5,
        enablePlaceholders = true,
        initialLoadSize = 10
    )


    init {
        loadPublishedBooks()
        totalCartItems = cartItemsRepo.getLiveTotalCartItemsNo(userId)

        viewModelScope.launch{
           publishedBooks = publishedBooksRepo.getPublishedBooks()

            if (publishedBooks.isEmpty()){
                remoteDataSource.getLiveListOfDataAsync(
                    RemoteDataFields.PUBLISHED_BOOKS_COLL,
                    PublishedBook::class.java,
                    RemoteDataFields.DATE_TIME_PUBLISHED
                ) {
                    addAllBooks(it)
                }
            }else{
                publishedBooks[0].publishedDate?.let { timestamp->
                    remoteDataSource.getLiveListOfDataAsyncFrom(
                        RemoteDataFields.PUBLISHED_BOOKS_COLL,
                        PublishedBook::class.java,
                        timestamp
                    ){
                        addAllBooks(it)
                    }
                }
            }
        }

    }

    fun getPublishedBooks(): List<PublishedBook> {
        return publishedBooks
    }

    private fun addAllBooks(publishedBooks: List<PublishedBook>){
        viewModelScope.launch {
            publishedBooksRepo.addAllPubBooks(publishedBooks)
        }
    }

    fun loadPublishedBooks(){
        if (connectionUtil.isConnected()){
            allPublishedBook = publishedBooksRepo.getLivePublishedBooks()
        }else{
            isNoConnection.value = true
        }
    }

    fun getLiveTotalCartItemsNo():LiveData<Int> {
        return totalCartItems
    }

    fun getIsNetworkError():LiveData<Boolean> {
        return isNetworkError
    }

    fun getIsNoConnection():LiveData<Boolean> {
        return isNoConnection
    }

    fun getAllPublishedBooks():LiveData<List<PublishedBook>> {
        return allPublishedBook
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