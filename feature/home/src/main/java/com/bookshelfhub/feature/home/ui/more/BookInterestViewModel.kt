package com.bookshelfhub.feature.home.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.core.model.entities.BookInterest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookInterestViewModel @Inject constructor(
    private val bookInterestRepo: IBookInterestRepo,
    val userAuth: IUserAuth
): ViewModel(){

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