package com.bookshelfhub.bookshelfhub.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val localDb: ILocalDb,
    private val database:Database,
    val userAuth: IUserAuth): ViewModel(){

    private val userId:String = userAuth.getUserId()
    private var user: LiveData<User> = MutableLiveData()

    init {
        user = localDb.getLiveUser(userId)
    }

    fun getUser(): LiveData<User> {
        return user
    }

    suspend fun addUser(user:User){
        database.addUser(user)
    }

}