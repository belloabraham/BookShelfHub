package com.bookshelfhub.bookshelfhub.data.repos.earnings

import com.bookshelfhub.bookshelfhub.data.models.Earnings

interface IEarningsRepo {
    suspend fun getRemoteEarnings(userId: String): Earnings?
}