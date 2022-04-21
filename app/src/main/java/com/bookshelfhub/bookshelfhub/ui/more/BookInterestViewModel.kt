package com.bookshelfhub.bookshelfhub.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.repos.bookinterest.IBookInterestRepo
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookInterestViewModel @Inject constructor(
    private val bookInterestRepo:IBookInterestRepo,
    val userAuth: IUserAuth): ViewModel(){

    private var bookInterest: LiveData<Optional<BookInterest>> = MutableLiveData()
    private val userId:String = userAuth.getUserId()

    init {
        bookInterest = bookInterestRepo.getLiveBookInterest(userId)
    }

    fun addBookInterest(bookInterest: BookInterest){
        viewModelScope.launch {
            bookInterestRepo.addBookInterest(bookInterest)
        }
    }

    fun getBookInterest(): LiveData<Optional<BookInterest>> {
        return bookInterest
    }
}