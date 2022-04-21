package com.bookshelfhub.bookshelfhub.data.repos.privatekeys

interface IPrivateKeysRepo {
    suspend fun <T : Any> getPrivateKeys(
        key: String,
        type: Class<T>,
    ): T?
}