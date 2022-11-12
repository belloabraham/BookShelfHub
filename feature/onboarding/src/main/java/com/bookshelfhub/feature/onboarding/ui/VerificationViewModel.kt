package com.bookshelfhub.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    ): ViewModel()  {

    private val _timerRemaining = MutableStateFlow(0L)
    val timerRemainingFromCountDown: StateFlow<Long> = _timerRemaining

    init {
        startCountDownTimer()
    }

     fun startCountDownTimer(durationInSeconds:Long=180, intervalInSec:Long=1){
        val interval = intervalInSec*1000
         viewModelScope.launch {
             while (durationInSeconds > 0) {
                 delay(interval)
                 durationInSeconds.minus(intervalInSec)
                 _timerRemaining.value = durationInSeconds
             }
         }
    }


}