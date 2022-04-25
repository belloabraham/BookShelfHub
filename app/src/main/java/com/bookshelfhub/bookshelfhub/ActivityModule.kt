package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.IntentUtil
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Firebase
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityScoped
    @Provides
    fun provideConnectionUtil(@ActivityContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }

    @ActivityScoped
    @Provides
    fun getDynamicLink(@ActivityContext context: Context): IDynamicLink {
        return Firebase(context.getString(R.string.dlink_domain_prefix), context)
    }

}