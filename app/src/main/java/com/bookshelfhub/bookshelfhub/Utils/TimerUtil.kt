package com.bookshelfhub.bookshelfhub.Utils

import android.os.CountDownTimer
import javax.inject.Inject

class TimerUtil @Inject constructor(private val intervalInMilliSec:Long) {

    fun startTimer(lengthInMilliSec:Long, getTimeRemaining: (Long)->Unit){

      object : CountDownTimer(lengthInMilliSec, intervalInMilliSec) {
            override fun onTick(millisUntilFinished: Long) {
                getTimeRemaining(millisUntilFinished/intervalInMilliSec)
            }
            override fun onFinish() {

            }
        }.start()

    }

}