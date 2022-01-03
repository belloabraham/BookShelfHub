package com.bookshelfhub.bookshelfhub.Utils

import android.app.Activity
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Lifecycle aware screen display manager
 */
class EnableWakeLock(val activity:Activity, val lifecycle: Lifecycle) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    /**
     * Keep the screen display on when activity in OnResume
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun keepScreenOn(){
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Disable keep the screen display on when activity in OnPause
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun clearKeepScreenOn(){
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}