package com.bookshelfhub.core.common.worker

import androidx.work.Constraints
import androidx.work.NetworkType

object Constraint {

    /**
     * Make sure there is an internet connection before starting the worker
     */
    fun getConnected(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
}