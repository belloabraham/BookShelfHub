package com.bookshelfhub.book.page

import androidx.work.*
import com.bookshelfhub.book.page.workers.DownloadBook
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.Book

class DownloadBookUseCase {

     operator fun invoke(
        worker: Worker, workData: Data
    ){

        val bookId = workData.getString(Book.ID)!!
        val foregroundDownloadBookWorker =
            OneTimeWorkRequestBuilder<DownloadBook>()
                .setConstraints(Constraint.getConnected())
                .setInputData(workData)
                .build()
        worker.enqueueUniqueWork(bookId, ExistingWorkPolicy.KEEP, foregroundDownloadBookWorker)
    }
}

