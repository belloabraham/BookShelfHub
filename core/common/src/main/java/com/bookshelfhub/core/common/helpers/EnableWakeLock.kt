package com.bookshelfhub.core.common.helpers

import android.app.Activity
import android.view.WindowManager
import androidx.lifecycle.*

/**
 * Lifecycle aware screen display manager
 */
class EnableWakeLock(private val activity:Activity,  val lifecycle: Lifecycle) : DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

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