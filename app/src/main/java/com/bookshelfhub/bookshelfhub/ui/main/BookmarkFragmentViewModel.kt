package com.bookshelfhub.bookshelfhub.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarkFragmentViewModel @Inject constructor(private val localDb: ILocalDb): ViewModel(){

    fun getLiveBookmarks(): LiveData<List<Bookmark>> {
        return localDb.getLiveBookmarks()
    }

}