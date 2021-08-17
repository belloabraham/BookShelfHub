package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.config.FirebaseRemoteConfig
import com.bookshelfhub.bookshelfhub.config.IRemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.FBUserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.Firestore
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.RoomDb
import com.bookshelfhub.bookshelfhub.services.notification.firebase.FirebaseCM
import com.bookshelfhub.bookshelfhub.services.notification.firebase.ICloudMessaging
import com.bookshelfhub.bookshelfhub.wrappers.Json
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.FirebaseDLink
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.IDynamicLink
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
    fun getRemoteConfig(): IRemoteConfig {
        return FirebaseRemoteConfig()
    }

    @Singleton
    @Provides
    fun getAppUtil(@ApplicationContext context: Context): AppUtil {
        return AppUtil(context)
    }

    @Singleton
    @Provides
    fun getLocalDb(@ApplicationContext context: Context): ILocalDb {
        return RoomDb(context)
    }

    @Singleton
    @Provides
    fun getCloudMessaging():ICloudMessaging{
        return FirebaseCM()
    }

    @Singleton
    @Provides
    fun getJson():Json{
        return Json(Gson())
    }

    @Singleton
    @Provides
    fun getCloudDb(json:Json): ICloudDb {
        return Firestore(json)
    }

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context, localDb: ILocalDb): Database {
        return Database(context,localDb )
    }

    @Singleton
    @Provides
    fun getUserAuthentication(): IUserAuth {
        return FBUserAuth()
    }

    @Singleton
    @Provides
    fun getSettingsUtil(@ApplicationContext context: Context): SettingsUtil {
        return SettingsUtil(context)
    }

}
