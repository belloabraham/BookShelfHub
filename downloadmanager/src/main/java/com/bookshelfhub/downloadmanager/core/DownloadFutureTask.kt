package com.bookshelfhub.downloadmanager.core

import com.bookshelfhub.downloadmanager.Priority
import com.bookshelfhub.downloadmanager.internal.DownloadRunnable
import java.util.concurrent.FutureTask


class DownloadFutureTask(private var downloadRunnable: DownloadRunnable) : FutureTask<DownloadRunnable>(downloadRunnable, null), Comparable<DownloadFutureTask>  {

    override fun compareTo(other: DownloadFutureTask): Int {
        val p1: Priority = downloadRunnable.priority
        val p2: Priority = other.downloadRunnable.priority
        return if (p1 === p2) downloadRunnable.sequence - other.downloadRunnable.sequence else p2.ordinal - p1.ordinal
    }

}