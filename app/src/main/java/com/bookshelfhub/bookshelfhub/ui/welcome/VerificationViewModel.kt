package com.bookshelfhub.bookshelfhub.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.helpers.utils.TimerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    val savedState: SavedStateHandle
    ): ViewModel()  {


    suspend fun countDownTime(durationInSeconds:Long=180, intervalInSec:Long=1): Flow<Long> {
        val interval = intervalInSec*1000
        return flow {
            while (durationInSeconds > 0) {
                delay(interval)
                durationInSeconds.minus(intervalInSec)
                emit(durationInSeconds)
            }
        }
    }


}