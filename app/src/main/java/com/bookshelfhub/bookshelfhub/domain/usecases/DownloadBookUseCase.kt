package com.bookshelfhub.bookshelfhub.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.work.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.models.uistate.BookDownloadState
import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.DownloadBook
import com.bookshelfhub.bookshelfhub.workers.Worker

class DownloadBookUseCase {

    suspend operator fun invoke(
        worker: Worker, workData: Data,
        bookDownloadStateRepo: IBookDownloadStateRepo
    ){
        val bookId = workData.getString(Book.ID)!!
        val expeditedDownloadBookWorker =
            OneTimeWorkRequestBuilder<DownloadBook>()
                .setConstraints(Constraint.getConnected())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workData)
                .build()
        worker.enqueueUniqueWork(bookId, ExistingWorkPolicy.KEEP, expeditedDownloadBookWorker)

        worker.getWorkInfoByIdLiveData(expeditedDownloadBookWorker.id).asFlow().collect{
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