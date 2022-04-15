package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.ReadHistoryDao
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.TimerUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun getTimerUtil(): TimerUtil {
        return TimerUtil(1000L)
    }

    @ViewModelScoped
    @Provides
    fun getConnectionUtil(@ApplicationContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }

    @ViewModelScoped
    @Provides
    fun providePaymentCardRepo(roomInstance:RoomInstance): PaymentCardRepo {
        return PaymentCardRepo(roomInstance.paymentCardDao())
    }

    @ViewModelScoped
    @Provides
    fun provideOrderedBooksRepo(roomInstance:RoomInstance, remoteDataSource: IRemoteDataSource): OrderedBooksRepo {
        return OrderedBooksRepo(roomInstance.orderedBooksDao(), remoteDataSource)
    }

    @ViewModelScoped
    @Provides
    fun provideReferralRepo(roomInstance:RoomInstance): ReferralRepo {
        return ReferralRepo(roomInstance.referralDao())
    }

    @ViewModelScoped
    @Provides
    fun provideReadHistory(roomInstance:RoomInstance): ReadHistoryRepo {
        return ReadHistoryRepo(roomInstance.readHistoryDao())
    }

    @ViewModelScoped
    @Provides
    fun provideSearHistoryRepo(roomInstance:RoomInstance): SearchHistoryRepo {
        return SearchHistoryRepo(roomInstance.searchHistoryDao())
    }

}