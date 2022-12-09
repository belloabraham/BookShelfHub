package com.bookshelfhub.book.page

import androidx.lifecycle.asFlow
import androidx.work.*
import com.bookshelfhub.book.page.workers.DownloadBook
import com.bookshelfhub.core.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.core.model.uistate.BookDownloadState
import com.bookshelfhub.core.common.worker.Constraint
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.Book

class DownloadBookUseCase {

    suspend operator fun invoke(
        worker: Worker, workData: Data,
        bookDownloadStateRepo: IBookDownloadStateRepo
    ){

        val bookId = workData.getString(Book.ID)!!
        val foregroundDownloadBookWorker =
            OneTimeWorkRequestBuilder<DownloadBook>()
                .setConstraints(Constraint.getConnected())
                .setInputData(workData)
                .build()
        worker.enqueueUniqueWork(bookId, ExistingWorkPolicy.KEEP, foregroundDownloadBookWorker)

        worker.getWorkInfoByIdLiveData(foregroundDownloadBookWorker.id).asFlow().collect{
             val workIsEnqueuedButNotStarted = it.state == WorkInfo.State.ENQUEUED
           if(workIsEnqueuedButNotStarted){
               val initialDownloadProgress = (0..10).random()
               //To give the user the impression that download
               // have started so they do not tap on the download button again
              bookDownloadStateRepo.addDownloadState(BookDownloadState(bookId, initialDownloadProgress))
           }
        }
    }
}

