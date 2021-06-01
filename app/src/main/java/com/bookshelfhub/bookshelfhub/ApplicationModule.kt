package com.bookshelfhub.bookshelfhub

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.Utils.StringUtils
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.wrapper.imageloader.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
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
    fun getUserAuthentication(): UserAuth {
        return UserAuth()
    }

    @Singleton
    @Provides
    fun getIntentUtil(@ApplicationContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

    @Singleton
    @Provides
    fun getNotificationHelper(@ApplicationContext context: Context, intentUtil:IntentUtil): NotificationHelper {
        val notifChannelId=context.getString(R.string.notif_channel_id)
        return NotificationHelper(context, notifChannelId,intentUtil, R.color.notf_color,R.drawable.ic_stat_bookshelfhub,R.drawable.notification_large_icon)
    }
}