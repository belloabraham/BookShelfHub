package com.bookshelfhub.bookshelfhub.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
    val userAuth: IUserAuth): ViewModel(){

    private val userId:String = userAuth.getUserId()
    private var user: LiveData<User> = MutableLiveData()

    init {
        user = userRepo.getLiveUser(userId)
    }

    fun getUser(): LiveData<User> {
        return user
    }

     fun addUser(user: User){
        viewModelScope.launch {
            userRepo.addUser(user)
        }
    }

}