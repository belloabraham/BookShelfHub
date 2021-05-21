package com.bookshelfhub.bookshelfhub.ui.welcome

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.Utils.TimerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WelcomeViewModel @Inject constructor(val timerUtil: TimerUtil) :ViewModel() {

    private var isTimerCompleted: MutableLiveData<Boolean>
    private var timeRemainingInMillis: MutableLiveData<Long>
    private var timerStarted: Boolean = false;

    init{
        isTimerCompleted  = MutableLiveData<Boolean>()
        timeRemainingInMillis  = MutableLiveData<Long>()
    }

    fun getTimerTimeRemaining():LiveData<Long>{
        return timeRemainingInMillis
    }

    fun getIsTimerCompleted():LiveData<Boolean>{
            return isTimerCompleted
    }

    fun startTimer(length: Long){
        if (!timerStarted){
            timerStarted=true
            timerUtil.startTimer(length, {
                timeRemainingInMillis.value=it
            },{
                isTimerCompleted.value=true
            })

        }
    }
}