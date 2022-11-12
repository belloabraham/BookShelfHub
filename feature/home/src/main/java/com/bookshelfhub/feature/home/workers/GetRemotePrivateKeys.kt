package com.bookshelfhub.feature.home.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.repos.private_keys.IPrivateKeysRepo
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.model.entities.ApiKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class GetRemotePrivateKeys  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val privateKeysRepo: IPrivateKeysRepo,
    private val settingsUtil: SettingsUtil
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
           ErrorUtil.e(e)
             Result.retry()
        }
    }
}