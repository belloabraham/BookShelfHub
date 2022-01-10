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
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.RoomDb
import com.bookshelfhub.bookshelfhub.services.notification.firebase.CloudMessaging
import com.bookshelfhub.bookshelfhub.services.notification.ICloudMessaging
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.Storage
import com.bookshelfhub.bookshelfhub.helpers.database.room.RoomInstance
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
import com.bookshelfhub.bookshelfhub.services.PrivateKeys
import com.bookshelfhub.bookshelfhub.services.database.Util
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
    fun provideStorage(): Storage {
        return Storage()
    }

    @Singleton
    @Provides
    fun providePrivateKeys(settingsUtil: SettingsUtil): PrivateKeys {
        return PrivateKeys(settingsUtil)
    }

    @Singleton
    @Provides
    fun provideDatabaseUtil(json:Json): Util {
        return Util(json)
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
    fun provideLocalDb(roomInstance:RoomInstance): ILocalDb {
        return RoomDb(roomInstance.userDao())
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
    fun provideCloudDb(json: Json): ICloudDb {
        return Firestore(json)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context, localDb: ILocalDb): Database {
        return Database(context, localDb)
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
