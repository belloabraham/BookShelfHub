package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.Fragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    val savedState: SavedStateHandle,
): ViewModel(){
    private val title =  savedState.get<String>(Book.NAME)!!
    private val fragmentId = savedState.get<Int>(Fragment.ID)!!
    private val bookId =  savedState.get<String>(Book.ID)!!

    fun getBookId(): String {
        return bookId
    }

    fun getFragmentId(): Int {
        return fragmentId
    }
    fun getTitle(): String {
        return title
    }
}