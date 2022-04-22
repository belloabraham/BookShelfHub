package com.bookshelfhub.bookshelfhub.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.helpers.utils.TimerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val timerUtil: TimerUtil,
    val savedState: SavedStateHandle
    ): ViewModel()  {

    private val IN_PROGRESS="in_progress"
    private var timeRemainingInMillis: MutableLiveData<Long> = MutableLiveData<Long>()
    private val timerDurationInMilliSec = 300_000 //5 Minutes
    private var inProgress = savedState.get<Boolean>(IN_PROGRESS)?: false

        init {
            //Just in the case the fragment got recreated, do not restart timer if timer was running before activity got recreated
            if (!inProgress){
                startTimer()
            }
        }

    fun setInProgress(value:Boolean){
        savedState.set(IN_PROGRESS, value)
    }

    fun getTimerTimeRemaining(): LiveData<Long> {
        return timeRemainingInMillis
    }

    private fun startTimer(){
        timerUtil.startTimer(timerDurationInMilliSec) {
            timeRemainingInMillis.value=it
        }
    }

}