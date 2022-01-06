package com.bookshelfhub.bookshelfhub.Utils

import android.app.Activity
import android.view.WindowManager
import androidx.lifecycle.*

/**
 * Lifecycle aware screen display manager
 */
class EnableWakeLock(private val activity:Activity, private val lifecycle: Lifecycle) : DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    /**
     * Keep the screen display on when activity in OnResume
     */
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Disable "keep the screen display on when activity in OnPause"
     */
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

}