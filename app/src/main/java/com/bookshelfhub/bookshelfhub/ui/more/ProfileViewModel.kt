package com.bookshelfhub.bookshelfhub.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
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