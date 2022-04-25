package com.bookshelfhub.bookshelfhub

import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.BookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookdownload.IBookDownloadStateRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookvideos.BookVideosRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookvideos.IBookVideosRepo
import com.bookshelfhub.bookshelfhub.data.repos.earnings.EarningsRepo
import com.bookshelfhub.bookshelfhub.data.repos.earnings.IEarningsRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymentcard.IPaymentCardRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymentcard.PaymentCardRepo
import com.bookshelfhub.bookshelfhub.data.repos.readhistory.IReadHistoryRepo
import com.bookshelfhub.bookshelfhub.data.repos.readhistory.ReadHistoryRepo
import com.bookshelfhub.bookshelfhub.data.repos.referral.IReferralRepo
import com.bookshelfhub.bookshelfhub.data.repos.referral.ReferralRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.SearchHistoryRepo
import com.bookshelfhub.bookshelfhub.data.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.CurrencyConversionAPI
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.ICurrencyConversionAPI
import com.bookshelfhub.bookshelfhub.helpers.webapi.retrofit.RetrofitInstance
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
    fun provideBookDownloadRepo(roomInstance:RoomInstance): IBookDownloadStateRepo {
        return BookDownloadStateRepo(roomInstance.bookDownloadStateDao())
    }


    @ViewModelScoped
    @Provides
    fun provideCurrencyConversionAPI(): ICurrencyConversionAPI {
        return CurrencyConversionAPI(RetrofitInstance.fixerConversionAPI)
    }

    @ViewModelScoped
    @Provides
    fun provideEarningsRepo(remoteDataSource: IRemoteDataSource): IEarningsRepo {
        return EarningsRepo(remoteDataSource)
    }

    @ViewModelScoped
    @Provides
    fun providePaymentCardRepo(roomInstance:RoomInstance): IPaymentCardRepo {
        return PaymentCardRepo(roomInstance.paymentCardDao())
    }


    @ViewModelScoped
    @Provides
    fun provideBookVideosRepo(remoteDataSource: IRemoteDataSource, roomInstance:RoomInstance): IBookVideosRepo {
        return BookVideosRepo(remoteDataSource, roomInstance.bookVideosDao())
    }

    @ViewModelScoped
    @Provides
    fun provideReferralRepo(roomInstance:RoomInstance): IReferralRepo {
        return ReferralRepo(roomInstance.referralDao())
    }

    @ViewModelScoped
    @Provides
    fun provideReadHistory(roomInstance:RoomInstance): IReadHistoryRepo {
        return ReadHistoryRepo(roomInstance.readHistoryDao())
    }

    @ViewModelScoped
    @Provides
    fun provideSearHistoryRepo(roomInstance:RoomInstance): ISearchHistoryRepo {
        return SearchHistoryRepo(roomInstance.searchHistoryDao())
    }

}