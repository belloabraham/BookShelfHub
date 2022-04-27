package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.data.models.entities.ApiKeys
import com.bookshelfhub.bookshelfhub.data.repos.privatekeys.IPrivateKeysRepo
import com.bookshelfhub.bookshelfhub.helpers.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class GetRemotePrivateKeys  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val privateKeysRepo: IPrivateKeysRepo,
    val settingsUtil: SettingsUtil
): CoroutineWorker(context,
    workerParams
) {

    override suspend fun doWork(): Result {

       return try {
            privateKeysRepo.getPrivateKeys(Settings.API_KEYS, ApiKeys::class.java)?.let {
                settingsUtil.setString(
                    Settings.PERSPECTIVE_API,
                    it.perspectiveKey!!
                )
                settingsUtil.setString(
                    Settings.FIXER_ACCESS_KEY,
                    it.fixerAccessKey!!
                )

                settingsUtil.setString(
                    Settings.PAYSTACK_LIVE_PUBLIC_KEY,
                    it.payStackLivePublicKey!!
                )
            }
             Result.success()
        }catch (e:Exception){
           Timber.e(e)
             Result.retry()
        }
    }
}