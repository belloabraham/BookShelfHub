package com.bookshelfhub.bookshelfhub

import android.content.Context
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.adapters.search.OrderedBooksAdapter
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationHelper
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.bookshelfhub.bookshelfhub.wrapper.Json
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityScoped
    @Provides
    fun getNotificationHelper(@ApplicationContext context: Context, intentUtil: IntentUtil): NotificationHelper {
        val notifChannelId=context.getString(R.string.notif_channel_id)
        return NotificationHelper(context, notifChannelId,intentUtil, R.color.notf_color,R.drawable.ic_stat_bookshelfhub,R.drawable.notification_large_icon)
    }

    @ActivityScoped
    @Provides
    fun getJson():Json{
        return Json()
    }
}