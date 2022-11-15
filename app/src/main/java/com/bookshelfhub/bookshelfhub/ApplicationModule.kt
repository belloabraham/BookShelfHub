package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.authentication.firebase.UserAuth
import com.bookshelfhub.core.cloud.messaging.ICloudMessaging
import com.bookshelfhub.core.cloud.messaging.firebase.CloudMessaging
import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.common.helpers.utils.AppUtil
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.bookinterest.BookInterestRepo
import com.bookshelfhub.core.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.core.data.repos.bookmarks.BookmarksRepo
import com.bookshelfhub.core.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.core.data.repos.cartitems.CartItemsRepo
import com.bookshelfhub.core.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.data.repos.ordered_books.OrderedBooksRepo
import com.bookshelfhub.core.data.repos.payment_transaction.IPaymentTransactionRepo
import com.bookshelfhub.core.data.repos.payment_transaction.PaymentTransactionRepo
import com.bookshelfhub.core.data.repos.private_keys.IPrivateKeysRepo
import com.bookshelfhub.core.data.repos.private_keys.PrivateKeysRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.data.repos.published_books.PublishedBooksRepo
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.data.repos.user.UserRepo
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.data.repos.user_review.UserReviewRepo
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.dynamic_link.IDynamicLink
import com.bookshelfhub.core.dynamic_link.firebase.FirebaseDynamicLink
import com.bookshelfhub.core.remote.cloud_functions.ICloudFunctions
import com.bookshelfhub.core.remote.cloud_functions.firebase.FirebaseCloudFunctions
import com.bookshelfhub.core.remote.database.IRemoteDataSource
import com.bookshelfhub.core.remote.database.firebase.Firestore
import com.bookshelfhub.core.remote.remote_config.Firebase
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.core.remote.storage.FirebaseCloudStorage
import com.bookshelfhub.core.remote.storage.ICloudStorage
import com.bookshelfhub.core.remote.webapi.retrofit.RetrofitInstance
import com.bookshelfhub.core.remote.webapi.wordtoxicityanalyzer.IWordAnalyzerAPI
import com.bookshelfhub.core.remote.webapi.wordtoxicityanalyzer.WordAnalyzerAPI
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideRemoteConfig(): IRemoteConfig {
        return Firebase()
    }

    @Singleton
    @Provides
    fun provideCloudStorage(@ApplicationContext context: Context): ICloudStorage {
        return FirebaseCloudStorage(context)
    }

    @Singleton
    @Provides
    fun provideOrderedBooksRepo(appDatabase:AppDatabase, remoteDataSource: IRemoteDataSource): IOrderedBooksRepo {
        return OrderedBooksRepo(appDatabase, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideCloudFunctions(): ICloudFunctions {
        return FirebaseCloudFunctions()
    }

    @Singleton
    @Provides
    fun provideWordAnalyzerAPI(): IWordAnalyzerAPI {
        return WordAnalyzerAPI(RetrofitInstance.perspectiveAPI)
    }

    @Singleton
    @Provides
    fun providePublishedBooksRepo(appDatabase: AppDatabase, remoteDataSource: IRemoteDataSource): IPublishedBooksRepo {
        return PublishedBooksRepo(appDatabase, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideUserReviewRepo(appDatabase: AppDatabase, remoteDataSource:IRemoteDataSource): IUserReviewRepo {
        return UserReviewRepo(appDatabase, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideWorker(@ApplicationContext context: Context): Worker {
        return Worker(context)
    }


    @Singleton
    @Provides
    fun provideAppUtil(@ApplicationContext context: Context): AppUtil {
        return AppUtil(context)
    }

    @Singleton
    @Provides
    fun providePrivateKeyRepo(): IPrivateKeysRepo {
        return PrivateKeysRepo()
    }

    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideUserRepo(appDatabase: AppDatabase, remoteDataSource: IRemoteDataSource, worker:Worker): IUserRepo {
        return UserRepo(appDatabase, remoteDataSource, worker)
    }

    @Singleton
    @Provides
    fun provideCartRepo(appDatabase: AppDatabase, worker: Worker): ICartItemsRepo {
        return CartItemsRepo(appDatabase, worker)
    }

    @Singleton
    @Provides
    fun providePaymentTransactionRepo(appDatabase: AppDatabase, worker: Worker): IPaymentTransactionRepo {
        return PaymentTransactionRepo(appDatabase, worker)
    }

    @Singleton
    @Provides
    fun provideBookmarkRepo(appDatabase: AppDatabase, worker:Worker, remoteDataSource: IRemoteDataSource): IBookmarksRepo {
        return BookmarksRepo(appDatabase, worker, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideBookInterestRepo(appDatabase: AppDatabase, remoteDataSource: IRemoteDataSource, worker: Worker): IBookInterestRepo {
        return BookInterestRepo(appDatabase, remoteDataSource, worker)
    }

    @Singleton
    @Provides
    fun provideCloudMessaging(): ICloudMessaging {
        return CloudMessaging()
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json(Gson())
    }

    @Singleton
    @Provides
    fun provideCloudDb(json: Json, connectionUtil: ConnectionUtil): IRemoteDataSource {
        return Firestore(json, connectionUtil)
    }

    @Singleton
    @Provides
    fun provideUserAuthentication(): IUserAuth {
        return UserAuth()
    }

    @Singleton
    @Provides
    fun provideSettingsUtil(@ApplicationContext context: Context): SettingsUtil {
        return SettingsUtil(context)
    }

    @Singleton
    @Provides
    fun getDynamicLink(@ApplicationContext context: Context): IDynamicLink {
        return FirebaseDynamicLink(
            context.getString(R.string.dlink_domain_prefix),
            context
        )
    }

    @Singleton
    @Provides
    fun provideConnectionUtil(@ApplicationContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }

}
