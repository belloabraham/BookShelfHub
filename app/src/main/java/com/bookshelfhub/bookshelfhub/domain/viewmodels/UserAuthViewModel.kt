package com.bookshelfhub.bookshelfhub.domain.viewmodels

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.data.models.entities.remote.RemoteUser
import com.bookshelfhub.bookshelfhub.data.repos.UserRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.UserAuth
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val userAuth: IUserAuth
    ): ViewModel(){

    private var isAddingUser: MutableLiveData<Boolean>  = MutableLiveData<Boolean>()
    private var isExistingUser: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var referrerId: String? =null
    private val userId = userAuth.getUserId()


     fun addRemoteUserData(remoteUser: RemoteUser){
         viewModelScope.launch {
             try {
                 userRepo.addRemoteUser(remoteUser, userId)
             }catch (e:Exception){
                 return@launch
             }
         }
    }

    fun getUserReferrerId():String?{
        return referrerId
    }
    fun setUserReferrerId(value:String?){
        referrerId=value
    }
    fun setIsExistingUser(value:Boolean){
        isExistingUser.value=value
    }

    fun getIsExistingUser(): LiveData<Boolean> {
        return isExistingUser
    }

    fun setIsAddingUser(value:Boolean){
        isAddingUser.value=value
    }

    fun getIsAddingUser(): LiveData<Boolean> {
        return isAddingUser
    }


}