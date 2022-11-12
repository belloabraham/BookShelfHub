package com.bookshelfhub.core.data.repos.private_keys

interface IPrivateKeysRepo {
    suspend fun <T : Any> getPrivateKeys(
        key: String,
        type: Class<T>,
    ): T?
}