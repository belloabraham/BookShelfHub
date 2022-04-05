package com.bookshelfhub.bookshelfhub.helpers.remoteconfig

interface IRemoteConfig {
    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun fetchConfigAsync(onComplete: (error: String?) -> Unit)
}