package com.bookshelfhub.bookshelfhub

import com.bookshelfhub.book.page.DownloadBookUseCase
import com.bookshelfhub.core.data.repos.earnings.EarningsRepo
import com.bookshelfhub.core.data.repos.earnings.IEarningsRepo
import com.bookshelfhub.core.data.repos.payment_card.IPaymentCardRepo
import com.bookshelfhub.core.data.repos.payment_card.PaymentCardRepo
import com.bookshelfhub.core.data.repos.read_history.IReadHistoryRepo
import com.bookshelfhub.core.data.repos.read_history.ReadHistoryRepo
import com.bookshelfhub.core.data.repos.search_history.ISearchHistoryRepo
import com.bookshelfhub.core.data.repos.search_history.SearchHistoryRepo
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.domain.usecases.GetBookIdFromCompoundId
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.webapi.currencyconverter.CurrencyConversionAPI
import com.bookshelfhub.core.remote.webapi.currencyconverter.ICurrencyConversionAPI
import com.bookshelfhub.core.remote.webapi.retrofit.RetrofitInstance
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
    fun provideReadHistory(appDatabase:AppDatabase): IReadHistoryRepo {
        return ReadHistoryRepo(appDatabase)
    }

    @ViewModelScoped
    @Provides
    fun provideSearHistoryRepo(appDatabase:AppDatabase): ISearchHistoryRepo {
        return SearchHistoryRepo(appDatabase)
    }

}