package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.core.common.helpers.KeyboardUtil
import com.bookshelfhub.core.common.helpers.clipboard.ClipboardHelper
import com.bookshelfhub.core.common.helpers.textlinkbuilder.TextLinkBuilder
import com.bookshelfhub.core.common.helpers.utils.IntentUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @FragmentScoped
    @Provides
    fun getKeyboardUtil(): KeyboardUtil {
        return KeyboardUtil()
    }


    @FragmentScoped
    @Provides
    fun getIntentUtil(@ActivityContext context: Context): IntentUtil {
        return IntentUtil(context)
    }

    @FragmentScoped
    @Provides
    fun getClipboardHelper(@ActivityContext context: Context): ClipboardHelper {
        return ClipboardHelper(context)
    }

    @FragmentScoped
    @Provides
    fun getTextLinkBuilder(): TextLinkBuilder {
        return TextLinkBuilder()
    }

}