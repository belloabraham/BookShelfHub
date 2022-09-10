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
import com.bookshelfhub.bookshelfhub.data.sources.local.AppDatabase
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.domain.usecases.DownloadBookUseCase
import com.bookshelfhub.bookshelfhub.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.CurrencyConversionAPI
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.ICurrencyConversionAPI
import com.bookshelfhub.bookshelfhub.helpers.webapi.retrofit.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {


    @ViewModelScoped
    @Provides
    fun provideBookDownloadStateRepo(appDatabase:AppDatabase): IBookDownloadStateRepo {
        return BookDownloadStateRepo(appDatabase)
    }

    @ViewModelScoped
    @Provides
    fun provideDownloadBookUseCase(): DownloadBookUseCase {
        return DownloadBookUseCase()
    }

    @ViewModelScoped
    @Provides
    fun provideGetBookIdCompoundId(): GetBookIdFromCompoundId {
        return GetBookIdFromCompoundId()
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
    fun providePaymentCardRepo(appDatabase:AppDatabase): IPaymentCardRepo {
        return PaymentCardRepo(appDatabase)
    }

    @ViewModelScoped
    @Provides
    fun provideBookVideosRepo(remoteDataSource: IRemoteDataSource, appDatabase:AppDatabase): IBookVideosRepo {
        return BookVideosRepo( appDatabase, remoteDataSource)
    }

    @ViewModelScoped
    @Provides
    fun provideReferralRepo(appDatabase:AppDatabase): IReferralRepo {
        return ReferralRepo(appDatabase)
    }

    @ViewModelScoped
    @Provides
    fun provideReadHistory(appDatabase:AppDatabase): IReadHistoryRepo {
        return ReadHistoryRepo(appDatabase)
    }

    @ViewModelScoped
    @Provides
    fun provideSearHistoryRepo(appDatabase:AppDatabase): ISearchHistoryRepo {
        return SearchHistoryRepo(appDatabase)
    }

}