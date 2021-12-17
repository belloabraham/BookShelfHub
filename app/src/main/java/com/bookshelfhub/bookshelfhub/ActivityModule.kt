package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Firebase
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
import com.bookshelfhub.bookshelfhub.services.wordtoxicity.Perspective
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {


    @ActivityScoped
    @Provides
    fun getPerspective(): Perspective {
        return Perspective()
    }

    @ActivityScoped
    @Provides
    fun getIntentUtil(@ActivityContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

    @ActivityScoped
    @Provides
    fun getDynamicLink(@ActivityContext context: Context): IDynamicLink {
        return Firebase(context.getString(R.string.dlink_domain_prefix), context)
    }

    @ActivityScoped
    @Provides
    fun getConnectionUtil(@ActivityContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }
}