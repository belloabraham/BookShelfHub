package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookinterest.BookInterestRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.BookmarksRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.CartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.cartitems.ICartItemsRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.OrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.IPaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.data.repos.paymenttransaction.PaymentTransactionRepo
import com.bookshelfhub.bookshelfhub.data.repos.privatekeys.IPrivateKeysRepo
import com.bookshelfhub.bookshelfhub.data.repos.privatekeys.PrivateKeysRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.PublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.IUserRepo
import com.bookshelfhub.bookshelfhub.data.repos.user.UserRepo
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import com.bookshelfhub.bookshelfhub.data.repos.userreview.UserReviewRepo
import com.bookshelfhub.bookshelfhub.helpers.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.Firebase
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.UserAuth
import com.bookshelfhub.bookshelfhub.data.sources.remote.Firestore
import com.bookshelfhub.bookshelfhub.data.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.helpers.notification.firebase.CloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.data.sources.local.RoomInstance
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.FirebaseCloudStorage
import com.bookshelfhub.bookshelfhub.helpers.cloudstorage.ICloudStorage
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.FirebaseDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.webapi.retrofit.RetrofitInstance
import com.bookshelfhub.bookshelfhub.helpers.webapi.wordtoxicityanalyzer.IWordAnalyzerAPI
import com.bookshelfhub.bookshelfhub.helpers.webapi.wordtoxicityanalyzer.WordAnalyzerAPI
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
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
    fun provideOrderedBooksRepo(roomInstance:RoomInstance, remoteDataSource: IRemoteDataSource): IOrderedBooksRepo {
        return OrderedBooksRepo(roomInstance, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideWordAnalyzerAPI(): IWordAnalyzerAPI {
        return WordAnalyzerAPI(RetrofitInstance.perspectiveAPI)
    }

    @Singleton
    @Provides
    fun providePublishedBooksRepo(roomInstance: RoomInstance, remoteDataSource: IRemoteDataSource): IPublishedBooksRepo {
        return PublishedBooksRepo(roomInstance, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideUserReviewRepo(roomInstance: RoomInstance, remoteDataSource:IRemoteDataSource): IUserReviewRepo {
        return UserReviewRepo(roomInstance, remoteDataSource)
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
    fun provideRoomInstance(@ApplicationContext context: Context): RoomInstance {
        return RoomInstance.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideUserRepo(roomInstance: RoomInstance, remoteDataSource: IRemoteDataSource, worker:Worker): IUserRepo {
        return UserRepo(roomInstance, remoteDataSource, worker)
    }

    @Singleton
    @Provides
    fun provideCartRepo(roomInstance: RoomInstance, worker: Worker): ICartItemsRepo {
        return CartItemsRepo(roomInstance, worker)
    }

    @Singleton
    @Provides
    fun providePaymentTransactionRepo(roomInstance: RoomInstance, worker: Worker, remoteDataSource: IRemoteDataSource): IPaymentTransactionRepo {
        return PaymentTransactionRepo(roomInstance, worker, remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideBookmarkRepo(roomInstance: RoomInstance, worker:Worker, remoteDataSource: IRemoteDataSource): IBookmarksRepo {
        return BookmarksRepo(roomInstance, worker, remoteDataSource)
    }


    @Singleton
    @Provides
    fun provideBookInterestRepo(roomInstance: RoomInstance, remoteDataSource: IRemoteDataSource, worker:Worker): IBookInterestRepo {
        return BookInterestRepo(roomInstance, remoteDataSource, worker)
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
