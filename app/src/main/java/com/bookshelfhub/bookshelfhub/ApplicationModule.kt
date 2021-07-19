package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.wrapper.Json
import com.bookshelfhub.bookshelfhub.wrapper.dynamiclink.DynamicLink
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
    fun getConnectionUtil(@ApplicationContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }

    @Singleton
    @Provides
    fun getRemoteConfig(): RemoteConfig {
        return RemoteConfig()
    }

    @Singleton
    @Provides
    fun getDynamicLink(@ApplicationContext context: Context, appUtil: AppUtil): DynamicLink {
        return DynamicLink(context.getString(R.string.dlink_domain_prefix), context, appUtil)
    }

    @Singleton
    @Provides
    fun getAppUtil(@ApplicationContext context: Context): AppUtil {
        return AppUtil(context)
    }

    @Singleton
    @Provides
    fun getLocalDb(@ApplicationContext context: Context): LocalDb {
        return LocalDb(context)
    }

    @Singleton
    @Provides
    fun getJson():Json{
        return Json(Gson())
    }

    @Singleton
    @Provides
    fun getCloudDb(json:Json): CloudDb {
        return CloudDb(json)
    }

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context, localDb: LocalDb): Database {
        return Database(context,localDb )
    }

    @Singleton
    @Provides
    fun getUserAuthentication(): UserAuth {
        return UserAuth()
    }

    @Singleton
    @Provides
    fun getSettingsUtil(@ApplicationContext context: Context): SettingsUtil {
        return SettingsUtil(context)
    }

    @Singleton
    @Provides
    fun getStringUtil(): StringUtil {
        return StringUtil()
    }

    @Singleton
    @Provides
    fun getIntentUtil(@ApplicationContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

}