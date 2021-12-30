package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.Fragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInfoActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle,
): ViewModel() {
    private val title = savedState.get<String>(Book.TITLE.KEY)!!
    private val fragmentId = savedState.get<Int>(Fragment.ID.KEY)!!
    private val isbn = savedState.get<String>(Book.ISBN.KEY)!!

    fun getIsbn(): String {
        return isbn
    }

    fun getFragmentId(): Int {
        return fragmentId
    }
    fun getTitle(): String {
        return title
    }

}