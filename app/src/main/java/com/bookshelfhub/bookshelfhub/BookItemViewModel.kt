package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookItemViewModel @Inject constructor(private val localDb: ILocalDb, val userAuth:IUserAuth ): ViewModel(){

    val userId = userAuth.getUserId()

    private val config  = PagingConfig(
        pageSize = 5,
        enablePlaceholders = true,
        initialLoadSize = 10
    )


    fun getLiveListOfCartItems(userId:String): LiveData<List<Cart>> {
        return localDb.getLiveListOfCartItems(userId)
    }

    fun getBooksByCategoryPageSource(category:String): Flow<PagingData<PublishedBooks>> {
        return Pager(config){
            localDb.getBooksByCategoryPageSource(category)
        }.flow
    }

}