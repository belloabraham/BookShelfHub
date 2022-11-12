package com.bookshelfhub.core.data.repos.earnings

import com.bookshelfhub.core.model.Earnings

interface IEarningsRepo {
    suspend fun getRemoteEarnings(userId: String): Earnings?
}