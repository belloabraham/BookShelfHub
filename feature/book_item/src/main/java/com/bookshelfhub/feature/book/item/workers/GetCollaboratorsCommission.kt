package com.bookshelfhub.feature.book.item.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.referral.IReferralRepo
import com.bookshelfhub.core.model.entities.Collaborator
import com.bookshelfhub.feature.book.item.CollabCommission
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GetCollaboratorsCommission  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val referralRepo: IReferralRepo,
    ): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val pubId = inputData.getString(Book.PUB_ID)!!
        val bookId = inputData.getString(Book.ID)!!
        val collabId = inputData.getString(CollabCommission.COLLAB_ID)!!

        val localCollaborator = referralRepo.getAnOptionalCollaborator(bookId)
        return try {
            if(localCollaborator.isPresent){
                val remoteCollaborator = referralRepo.getARemoteCollaborator(pubId, collabId, bookId)
                if(remoteCollaborator != null){
                    val collaborator = Collaborator(collabId, bookId, remoteCollaborator.collabCommissionInPercent)
                    referralRepo.addCollaboratorOrReplace(collaborator)
                }
            }

            Result.success()
        }catch (e:Exception){
            ErrorUtil.e(e)
            Result.retry()
        }
    }
}