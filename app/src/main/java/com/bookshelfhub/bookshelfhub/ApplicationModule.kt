package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.services.remoteconfig.Firebase
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.Firestore
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.RoomDb
import com.bookshelfhub.bookshelfhub.services.notification.firebase.CloudMessaging
import com.bookshelfhub.bookshelfhub.services.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
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
        return Firebase()
    }

    @Singleton
    @Provides
    fun getWebAPI(): WebApi {
        return WebApi()
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
    fun getCloudMessaging(): ICloudMessaging {
        return CloudMessaging()
    }

    @Singleton
    @Provides
    fun getJson(): Json {
        return Json(Gson())
    }

    @Singleton
    @Provides
    fun getCloudDb(json: Json): ICloudDb {
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
        return UserAuth()
    }

    @Singleton
    @Provides
    fun getSettingsUtil(@ApplicationContext context: Context): SettingsUtil {
        return SettingsUtil(context)
    }

}
