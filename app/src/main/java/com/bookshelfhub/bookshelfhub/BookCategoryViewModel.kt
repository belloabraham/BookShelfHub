package com.bookshelfhub.bookshelfhub

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookCategoryViewModel @Inject constructor(private val localDb: ILocalDb, val userAuth: IUserAuth) : ViewModel(){

    private var liveBooksByCategory: LiveData<List<PublishedBooks>> = MutableLiveData()

    private val userId = userAuth.getUserId()

    private val config  = PagingConfig(
        pageSize = 10,
        prefetchDistance=5,
        enablePlaceholders = true,
        initialLoadSize = 20
    )

    fun loadLiveBooksByCategory(category: String, context: Context){
        liveBooksByCategory = if (category==context.getString(R.string.trending)){
            localDb.getLiveTrendingBooks()
        }else if (category == context.getString(R.string.recommended_for)){
            localDb.getLiveRecommendedBooks()
        }else{
            localDb.getLiveBooksByCategory(category)
        }
    }

    fun getLiveTotalCartItemsNo(): LiveData<Int> {
        return localDb.getLiveTotalCartItemsNo(userId)
    }

    fun getTrendingBooks(): Flow<PagingData<PublishedBooks>> {
        return  Pager(config){
            localDb.getTrendingBooksPageSource()
        }.flow
    }

    fun getRecommendedBooks(): Flow<PagingData<PublishedBooks>> {
        return  Pager(config){
            localDb.getRecommendedBooksPageSource()
        }.flow
    }

    fun getBooksByCategory(category: String): Flow<PagingData<PublishedBooks>> {
        return  Pager(config){
            localDb.getBooksByCategoryPageSource(category)
        }.flow
    }

    fun getLiveBooksByCategory(): LiveData<List<PublishedBooks>> {
        return liveBooksByCategory
    }

}
