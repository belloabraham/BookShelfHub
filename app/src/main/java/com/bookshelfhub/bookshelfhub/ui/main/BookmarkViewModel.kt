package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import com.bookshelfhub.bookshelfhub.data.repos.BookmarksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    val userAuth:IUserAuth,
    private val bookmarksRepo: BookmarksRepo,
    ): ViewModel(){

    val userId = userAuth.getUserId()
    private var liveBookmarks: LiveData<List<Bookmark>> = MutableLiveData()


    init{
       liveBookmarks = bookmarksRepo.getLiveBookmarks()
    }

    fun addBookmark(bookmark: Bookmark){
        viewModelScope.launch {
            bookmarksRepo.addBookmark(bookmark)
        }
    }

    fun getLiveBookmarks():LiveData<List<Bookmark>> {
        return liveBookmarks
    }

}