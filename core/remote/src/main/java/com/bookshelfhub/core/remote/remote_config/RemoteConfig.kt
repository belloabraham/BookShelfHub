package com.bookshelfhub.core.remote.remote_config

import com.bookshelfhub.core.remote.Config
import com.bookshelfhub.core.remote.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfig {
    const val BOOK_REPORT_URL = "book_report_url"
    const val EMAIL="email"
    const val PHONE="phone"
    const val ENABLE_TRENDING = "enable_trending"

    fun initialize(){
        val remoteConfig = Firebase.remoteConfig
        if(Config.isDevMode()){
            val configSettings = remoteConfigSettings {
                this.minimumFetchIntervalInSeconds = 1800L
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }
        remoteConfig.setDefaultsAsync(R.xml.firebase_remote_config_defaults).addOnCompleteListener {
            remoteConfig.fetchAndActivate()
        }
    }
}