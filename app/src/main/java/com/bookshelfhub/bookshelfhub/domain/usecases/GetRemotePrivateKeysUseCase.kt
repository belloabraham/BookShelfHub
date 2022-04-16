package com.bookshelfhub.bookshelfhub.domain.usecases

import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.data.models.ApiKeys
import com.bookshelfhub.bookshelfhub.data.repos.PrivateKeysRepo
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetRemotePrivateKeysUseCase @Inject constructor(private val privateKeysRepo: PrivateKeysRepo, val settingsUtil: SettingsUtil) {

    suspend operator fun invoke(){
            try {
                privateKeysRepo.getPrivateKeys(Settings.API_KEYS, ApiKeys::class.java)?.let {
                    settingsUtil.setString(
                        Settings.PERSPECTIVE_API,
                        it.perspectiveKey!!
                    )
                    settingsUtil.setString(
                        Settings.FIXER_ENDPOINT,
                        it.fixerEndpoint!!
                    )
                    settingsUtil.setString(
                        Settings.FLUTTER_ENCRYPTION,
                        it.flutterEncKey!!
                    )
                    settingsUtil.setString(
                        Settings.FLUTTER_PUBLIC,
                        it.flutterPublicKey!!
                    )
                }
            }catch (e:Exception){ }
    }


}