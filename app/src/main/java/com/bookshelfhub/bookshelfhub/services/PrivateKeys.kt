package com.bookshelfhub.bookshelfhub.services

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.bookshelfhub.bookshelfhub.Utils.settings.Settings
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.models.ApiKeys
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PrivateKeys(private val settingsUtil: SettingsUtil) {

    private val database = Firebase.database.reference

    private fun <T : Any> get(
        activity: Activity,
        key: String,
        type: Class<T>,
        onSuccess: (keys: T?) -> Unit
    ) {
        database.child(key).get().addOnSuccessListener(activity) {
            if (it.exists()) {
                val data = it.getValue(type)
                onSuccess(data)
            }
        }
    }


    fun loadPrivateKeys(lifecycleScope: LifecycleCoroutineScope, activity: Activity) {
        lifecycleScope.launch(IO) {
            val perspectiveKey = settingsUtil.getString(Settings.PERSPECTIVE_API.KEY)
            if (perspectiveKey == null) {
                withContext(Main) {
                    get(activity, Settings.API_KEYS.KEY, ApiKeys::class.java) {
                        it?.let {
                            lifecycleScope.launch(IO) {
                                settingsUtil.setString(
                                    Settings.PERSPECTIVE_API.KEY,
                                    it.perspectiveKey!!
                                )
                                settingsUtil.setString(
                                    Settings.FIXER_ENDPOINT.KEY,
                                    it.fixerEndpoint!!
                                )
                                settingsUtil.setString(
                                    Settings.FLUTTER_ENCRYPTION.KEY,
                                    it.flutterEncKey!!
                                )
                                settingsUtil.setString(
                                    Settings.FLUTTER_PUBLIC.KEY,
                                    it.flutterPublicKey!!
                                )
                            }
                        }
                    }
                }

            }
        }
    }


}