package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkFragmentViewModel @Inject constructor(private val localDb: ILocalDb, val userAuth:IUserAuth): ViewModel(){

    val userId = userAuth.getUserId()


    init{
        val bookmark = listOf(
            Bookmark( userId, "1234", 4,"Hello its me"),
            Bookmark( userId, "12345", 4,"Hello PPO"),
            Bookmark( userId, "12346", 4,"Hello Hi"),
            Bookmark( userId, "12347", 4,"Hello Howdy"),
        )
        viewModelScope.launch {
            localDb.addBookmarkList(bookmark)
        }
    }


    fun getLiveBookmarks(userId:String):LiveData<List<Bookmark>> {
        return localDb.getLiveBookmarks(userId)
    }

}