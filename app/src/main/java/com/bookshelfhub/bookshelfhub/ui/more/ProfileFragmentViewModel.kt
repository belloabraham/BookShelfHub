package com.bookshelfhub.bookshelfhub.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(val localDb: LocalDb, val userAuth: UserAuth): ViewModel(){
    private var user: LiveData<User> = MutableLiveData()
    private val userId:String = userAuth.getUserId()

    init {
        user = localDb.getLiveUser(userId)
    }

    fun getUser(): LiveData<User> {
        return user
    }

}