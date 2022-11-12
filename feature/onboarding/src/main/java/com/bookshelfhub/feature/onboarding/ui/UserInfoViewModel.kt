package com.bookshelfhub.feature.onboarding.ui

import androidx.lifecycle.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.model.entities.remote.RemoteUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val userRepo: IUserRepo,
    private val userAuth: IUserAuth
    ): ViewModel()  {

    private var isAddingUser: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var isExistingUser: MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    fun setIsUserDataAlreadyInRemoteDatabase(value:Boolean){
        isExistingUser.value=value
    }

    fun getIsUserDataAlreadyInRemoteDatabase(): LiveData<Boolean> {
        return isExistingUser
    }


    fun addRemoteAndLocalUser(remoteUser: RemoteUser){
        viewModelScope.launch {
            try {
                userRepo.uploadRemoteUser(remoteUser, userAuth.getUserId())
                remoteUser.user.uploaded=true
                userRepo.addUser(remoteUser.user)

                setIsAddingUser(false)
            }catch (e:Exception){
                ErrorUtil.e(e)
                return@launch
            }
        }
    }

    fun setIsAddingUser(value:Boolean){
        isAddingUser.value=value
    }

    fun getIsAddingUser(): LiveData<Boolean> {
        return isAddingUser
    }


}