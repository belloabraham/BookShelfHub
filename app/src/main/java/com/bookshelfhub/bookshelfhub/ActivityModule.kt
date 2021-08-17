package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.FirebaseDLink
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.IDynamicLink
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
    fun getIntentUtil(@ActivityContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

    @ActivityScoped
    @Provides
    fun getDynamicLink(@ActivityContext context: Context, appUtil: AppUtil): IDynamicLink {
        return FirebaseDLink(context.getString(R.string.dlink_domain_prefix), context, appUtil)
    }

    @ActivityScoped
    @Provides
    fun getConnectionUtil(@ActivityContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }
}