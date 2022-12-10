package com.bookshelfhub.feature.home.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.data.repos.search_history.ISearchHistoryRepo
import com.bookshelfhub.core.model.entities.StoreSearchHistory
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.core.remote.remote_config.RemoteConfig
import com.bookshelfhub.feature.home.workers.RecommendedBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val publishedBooksRepo: IPublishedBooksRepo,
    cartItemsRepo: ICartItemsRepo,
    private val remoteConfig: IRemoteConfig,
    private val worker: Worker,
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

        viewModelScope.launch {
            try {
                val totalNoOfLocallyCachedPublishedBooks =  loadRemotePublishedBooks()
                loadRemotePublishedBooksForFirstTime(totalNoOfLocallyCachedPublishedBooks)
                isBookLoadSuccessfully.value = true
            }catch (e:Exception){
                ErrorUtil.e(e)
                isBookLoadSuccessfully.value = false
            }
        }
    }

     fun setIsBookLoadSuccessfully(value:Boolean){
        isBookLoadSuccessfully.value = value
    }

    private suspend fun loadRemotePublishedBooksForFirstTime(totalNoOfLocallyCachedPublishedBooks:Int){
        val noLocallyCachedPublishedBooks = totalNoOfLocallyCachedPublishedBooks <= 0
        if(noLocallyCachedPublishedBooks){
            val publishedBooks = publishedBooksRepo.getRemotePublishedBooks()
            publishedBooksRepo.addAllPubBooks(publishedBooks)
            if(publishedBooks.isNotEmpty()){
                updatedRecommendedBooks()
            }
        }
    }

    suspend fun loadRemotePublishedBooks(): Int {
            var totalNoOfLocalPublishedBooks = publishedBooksRepo.getTotalNoOfPublishedBooks()
            val thereAreLocallyCachedPublishedBooks = totalNoOfLocalPublishedBooks > 0

            if(thereAreLocallyCachedPublishedBooks){
                val publishedBooks = publishedBooksRepo.getRemotePublishedBooksFrom(
                    fromSerialNo =   ++totalNoOfLocalPublishedBooks
                )
                publishedBooksRepo.addAllPubBooks(publishedBooks)
                if(publishedBooks.isNotEmpty()){
                    updatedRecommendedBooks()
                }
            }
        return totalNoOfLocalPublishedBooks
    }

    private fun updatedRecommendedBooks(){
        val recommendedBooksWorker =
            OneTimeWorkRequestBuilder<RecommendedBooks>()
                .build()
        worker.enqueueUniqueWork(
            Tag.recommendedBooksWorker,
            ExistingWorkPolicy.REPLACE,
            recommendedBooksWorker
        )
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