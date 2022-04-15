package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.Firebase
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.UserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.Firestore
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.notification.firebase.CloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import com.bookshelfhub.bookshelfhub.helpers.SecreteKeys
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.Util
import com.bookshelfhub.bookshelfhub.workers.Worker
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
    fun providePrivateKeys(settingsUtil: SettingsUtil): SecreteKeys {
        return SecreteKeys(settingsUtil)
    }

    @Singleton
    @Provides
    fun provideDatabaseUtil(json:Json): Util {
        return Util(json)
    }

    @Singleton
    @Provides
    fun providePublishedBooksRepo(roomInstance: RoomInstance): PublishedBooksRepo {
        return PublishedBooksRepo(roomInstance.publishedBooksDao())
    }

    @Singleton
    @Provides
    fun provideUserReviewRepo(roomInstance: RoomInstance, remoteDataSource:IRemoteDataSource): UserReviewRepo {
        return UserReviewRepo(roomInstance.userReviewsDao(), remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideWorker(@ApplicationContext context: Context): Worker {
        return Worker(context)
    }

    @Singleton
    @Provides
    fun provideWebAPI(): WebApi {
        return WebApi()
    }

    @Singleton
    @Provides
    fun provideAppUtil(@ApplicationContext context: Context): AppUtil {
        return AppUtil(context)
    }


    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context): RoomInstance {
        return RoomInstance.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideUserRepo(roomInstance: RoomInstance, worker:Worker): UserRepo {
        return UserRepo(roomInstance.userDao(), worker)
    }

    @Singleton
    @Provides
    fun provideCartRepo(roomInstance: RoomInstance): CartItemsRepo {
        return CartItemsRepo(roomInstance.cartItemsDao())
    }

    @Singleton
    @Provides
    fun providePaymentTransactionRepo(roomInstance: RoomInstance, worker: Worker): PaymentTransactionRepo {
        return PaymentTransactionRepo(roomInstance.paymentTransDao(), worker)
    }

    @Singleton
    @Provides
    fun provideBookmarkRepo(roomInstance: RoomInstance, worker:Worker, remoteDataSource: IRemoteDataSource): BookmarksRepo {
        return BookmarksRepo(roomInstance.bookmarksDao(), worker, remoteDataSource)
    }


    @Singleton
    @Provides
    fun provideBookInterestRepo(roomInstance: RoomInstance, worker:Worker): BookInterestRepo {
        return BookInterestRepo(roomInstance.bookInterestDao(), worker)
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
    fun provideCloudDb(json: Json): IRemoteDataSource {
        return Firestore(json)
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

}
