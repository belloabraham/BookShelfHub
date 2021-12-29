package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(private val localDb: ILocalDb): ViewModel(){

    fun deleteUserData(){
        viewModelScope.launch(IO) {
            localDb.deleteUserRecord()
            localDb.deleteAllReviews()
            localDb.deleteAllOrderedBooks()
            localDb.deleteAllBookmarks()
        }
    }

    fun deleteAllPaymentCards(){
        viewModelScope.launch(IO) {
            localDb.deleteAllPaymentCards()
        }
    }

}