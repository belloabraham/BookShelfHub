package com.bookshelfhub.bookshelfhub.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInterestViewModel @Inject constructor(val localDb: ILocalDb, val userAuth: IUserAuth): ViewModel(){

    private var bookInterest: LiveData<Optional<BookInterest>> = MutableLiveData()
    private val userId:String = userAuth.getUserId()

    init {
        bookInterest = localDb.getLiveBookInterest(userId)

    }

    fun getBookInterest(): LiveData<Optional<BookInterest>> {
        return bookInterest
    }
}