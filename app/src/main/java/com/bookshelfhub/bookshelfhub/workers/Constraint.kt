package com.bookshelfhub.bookshelfhub.workers

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder

class Constraint {

    companion object{
        fun getConnected(): Constraints {
           return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }
    }

}