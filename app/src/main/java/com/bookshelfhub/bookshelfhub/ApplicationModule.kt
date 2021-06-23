package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
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
    fun getCloudDb(): CloudDb {
        return CloudDb()
    }

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context, localDb: LocalDb): Database {
        return Database(context,localDb )
    }

    @Singleton
    @Provides
    fun getUserAuthentication(stringUtil: StringUtil): UserAuth {
        return UserAuth(stringUtil)
    }

    @Singleton
    @Provides
    fun getSettingsUtil(@ApplicationContext context: Context): SettingsUtil {
        return SettingsUtil(context)
    }

    @Singleton
    @Provides
    fun getStringUtils(): StringUtil {
        return StringUtil()
    }

    @Singleton
    @Provides
    fun getIntentUtil(@ApplicationContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

}