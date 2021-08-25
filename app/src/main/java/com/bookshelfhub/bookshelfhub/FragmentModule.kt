package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.Utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.helpers.clipboard.ClipboardHelper
import com.bookshelfhub.bookshelfhub.helpers.textlinkbuilder.TextLinkBuilder
import com.bookshelfhub.bookshelfhub.views.tooltip.ToolTip
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
    fun getKeyboardUtil():KeyboardUtil{
        return KeyboardUtil()
    }

    @FragmentScoped
    @Provides
    fun getClipboardHelper(@ActivityContext context: Context): ClipboardHelper {
        return ClipboardHelper(context)
    }

    @FragmentScoped
    @Provides
    fun getDeviceUtil():DeviceUtil{
        return DeviceUtil()
    }

    @FragmentScoped
    @Provides
    fun getToolTip(@ActivityContext context: Context):ToolTip{
        return ToolTip(context)
    }

    @FragmentScoped
    @Provides
    fun getTextLinkBuilder(): TextLinkBuilder {
        return TextLinkBuilder()
    }


}