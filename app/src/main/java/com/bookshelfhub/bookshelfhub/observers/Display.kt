package com.bookshelfhub.bookshelfhub.observers

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bookshelfhub.bookshelfhub.extensions.clearKeepScreenOn
import com.bookshelfhub.bookshelfhub.extensions.keepScreenOn
import com.bookshelfhub.bookshelfhub.extensions.showToast

class Display(val activity:Activity, val lifecycle: Lifecycle) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun keepScreenOn(){
        activity.keepScreenOn()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun clearKeepScreenOn(){
        activity.clearKeepScreenOn()
    }
}