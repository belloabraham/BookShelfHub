package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.adapters.slider.SliderAdapter
import com.bookshelfhub.bookshelfhub.wrapper.imageloader.ImageLoader
import com.bookshelfhub.bookshelfhub.wrapper.tooltip.ToolTip
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @FragmentScoped
    @Provides
    fun getSliderAdapter(imageLoader: ImageLoader):SliderAdapter{
        return SliderAdapter(imageLoader)
    }

    @FragmentScoped
    @Provides
    fun getImageLoader():ImageLoader{
        return ImageLoader()
    }

    @FragmentScoped
    @Provides
    fun getKeyboardUtil():KeyboardUtil{
        return KeyboardUtil()
    }

    @FragmentScoped
    @Provides
    fun getToolTip(@ActivityContext context: Context):ToolTip{
        return ToolTip(context)
    }


}