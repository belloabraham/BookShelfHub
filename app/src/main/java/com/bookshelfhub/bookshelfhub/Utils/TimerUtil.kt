package com.bookshelfhub.bookshelfhub.Utils

import android.os.CountDownTimer
import javax.inject.Inject

class TimerUtil @Inject constructor(private val intervalInMilliSec:Long) {

    fun startTimer(length:Long, getTimeRemaining: (Long)->Unit){

      object : CountDownTimer(length, intervalInMilliSec) {
            override fun onTick(millisUntilFinished: Long) {
                val timeRemainingInSec = millisUntilFinished/intervalInMilliSec
                getTimeRemaining(timeRemainingInSec)
            }
            override fun onFinish() {

            }
        }.start()

    }

}