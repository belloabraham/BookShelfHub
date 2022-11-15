package com.bookshelfhub.feature.home.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.model.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: IUserRepo,
    val userAuth: IUserAuth
): ViewModel(){

    private val userId:String = userAuth.getUserId()
    private var liveUser: MutableLiveData<User> = MutableLiveData()
    private var user:User? = null

    fun loadUserData(){
        viewModelScope.launch {
            if(user==null){
                user = userRepo.getUser(userId).get()
            }
            liveUser.value = user!!
        }
    }

    fun getUser(): User? {
        return user
    }


    fun getLiveUser(): LiveData<User> {
        return liveUser
    }

     fun addUser(user: User){
        viewModelScope.launch {
            userRepo.addUser(user)
        }
    }

}