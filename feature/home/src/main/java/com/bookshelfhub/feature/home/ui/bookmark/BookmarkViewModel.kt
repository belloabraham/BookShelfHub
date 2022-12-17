package com.bookshelfhub.feature.home.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.core.model.entities.Bookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    val userAuth: IUserAuth,
    private val bookmarksRepo: IBookmarksRepo,
    ): ViewModel(){

    val userId = userAuth.getUserId()
    private var liveBookmarks: LiveData<List<Bookmark>> = MutableLiveData()

    init{
        liveBookmarks = bookmarksRepo.getLiveBookmarks(isDeleted = false)
        viewModelScope.launch {
            bookmarksRepo.loadRemoteBookmarks()
        }
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