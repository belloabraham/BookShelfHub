package com.bookshelfhub.core.remote.remote_config

interface IRemoteConfig {
    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun fetchConfigAsync(onComplete: (error: String?) -> Unit)
}