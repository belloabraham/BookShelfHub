package com.bookshelfhub.downloadmanager.core

import android.os.Process
import java.util.concurrent.ThreadFactory

class PriorityThreadFactory(private val threadPriority: Int): ThreadFactory{

    override fun newThread(runnable: Runnable): Thread {
        val wrapperRunnable = Runnable {
            try {
                Process.setThreadPriority(threadPriority)
            } catch (ignored: Throwable) {
            }
            runnable.run()
        }
        return Thread(wrapperRunnable)
    }
    
}