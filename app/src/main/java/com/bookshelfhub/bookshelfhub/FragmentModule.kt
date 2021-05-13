package com.bookshelfhub.bookshelfhub

import com.bookshelfhub.bookshelfhub.adapters.slider.SliderAdapter
import com.bookshelfhub.bookshelfhub.wrapper.imageloader.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
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
}