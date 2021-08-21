package com.bookshelfhub.bookshelfhub.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(private val cloudDb: ICloudDb, private val localDb: ILocalDb, val connectionUtil: ConnectionUtil, val userAuth: IUserAuth): ViewModel() {

    private var allPublishedBook : LiveData<List<PublishedBook>> = MutableLiveData()
    private var isNoConnection : MutableLiveData<Boolean> = MutableLiveData()
    private var isNetworkError : MutableLiveData<Boolean> = MutableLiveData()
    private val userId = userAuth.getUserId()
    private var totalCartItems : LiveData<Int> = MutableLiveData()


    private val config  = PagingConfig(
        pageSize = 5,
        enablePlaceholders = true,
        initialLoadSize = 10
    )


    init {
        loadPublishedBooks()
        totalCartItems = localDb.getLiveTotalCartItemsNo(userId)
    }

    fun addAllBooks(publishedBooks: List<PublishedBook>){
        viewModelScope.launch(IO) {
            localDb.addAllPubBooks(publishedBooks)
        }
    }

    fun loadPublishedBooks(){
        if (connectionUtil.isConnected()){
            allPublishedBook = localDb.getLivePublishedBooks()
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
            localDb.getTrendingBooksPageSource()
        }.flow
    }

    fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBook>> {
        return Pager(config){
            localDb.getBooksByCategoryPageSource(category)
        }.flow
    }

    fun getRecommendedBooksPageSource(): Flow<PagingData<PublishedBook>> {
        return Pager(config){
            localDb.getRecommendedBooksPageSource()
        }.flow
    }


}