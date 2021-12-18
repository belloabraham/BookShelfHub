package com.bookshelfhub.downloadmanager.core

import com.bookshelfhub.downloadmanager.internal.DownloadRunnable
import java.util.concurrent.*

class DownloadExecutor(maxNumThreads: Int, threadFactory: ThreadFactory?): ThreadPoolExecutor(maxNumThreads, maxNumThreads, 0, TimeUnit.MILLISECONDS,
    PriorityBlockingQueue<Runnable>(), threadFactory){

    override fun submit(task: Runnable?): Future<*>{
        val futureTask = DownloadFutureTask(task as DownloadRunnable)
        execute(futureTask)
        return futureTask
    }

}