package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.data.models.entities.Bookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(private val localDb: ILocalDb, val userAuth:IUserAuth ): ViewModel(){

    val userId = userAuth.getUserId()
    private var liveBookmarks: LiveData<List<Bookmark>> = MutableLiveData()


    init{
       liveBookmarks = localDb.getLiveBookmarks()
    }

    fun addBookmark(bookmark: Bookmark){
        viewModelScope.launch(IO) {
            localDb.addBookmark(bookmark)
        }
    }

    fun getLiveBookmarks():LiveData<List<Bookmark>> {
        return liveBookmarks
    }

}