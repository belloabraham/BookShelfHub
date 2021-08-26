package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(private val localDb: ILocalDb): ViewModel(){

    fun deleteUserData(){
        viewModelScope.launch(IO) {
            localDb.deleteUserRecord()
            localDb.deleteAllReviews()
            localDb.deleteAllOrderedBooks()
            localDb.deleteAllPaymentCards()
            localDb.deleteAllBookmarks()
        }
    }


    fun deleteAllPaymentCards(){
        viewModelScope.launch(IO) {
            localDb.deleteAllPaymentCards()
        }
    }
}