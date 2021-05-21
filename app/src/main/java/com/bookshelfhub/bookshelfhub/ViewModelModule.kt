package com.bookshelfhub.bookshelfhub

import com.bookshelfhub.bookshelfhub.Utils.TimerUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {


    @ViewModelScoped
    @Provides
    fun getTimerUtil(): TimerUtil {
        return TimerUtil(1000L)
    }

}