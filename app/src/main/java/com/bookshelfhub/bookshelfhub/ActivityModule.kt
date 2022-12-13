package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.core.common.helpers.textlinkbuilder.TextLinkBuilder
import com.bookshelfhub.core.common.helpers.utils.IntentUtil
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
    fun getTextLinkBuilder(): TextLinkBuilder {
        return TextLinkBuilder()
    }

    @ActivityScoped
    @Provides
    fun getIntentUtil(@ActivityContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

}