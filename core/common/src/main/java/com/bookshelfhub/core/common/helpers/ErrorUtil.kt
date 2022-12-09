package com.bookshelfhub.core.common.helpers

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber.DebugTree
import timber.log.Timber

object ErrorUtil {
    fun  e(t:Throwable?){
        Timber.e(t)
        t?.let {
            Firebase.crashlytics.recordException(it)
            Firebase.crashlytics.log(it.stackTraceToString())
        }
    }

    fun initialize(){
        Timber.plant(DebugTree())
    }
}