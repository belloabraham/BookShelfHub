package com.bookshelfhub.core.common.helpers

import timber.log.Timber.DebugTree
import timber.log.Timber

object ErrorUtil {
    fun  e(t:Throwable?){
        Timber.e(t)
    }

    fun initialize(){
        Timber.plant(DebugTree())
    }
}