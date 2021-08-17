package com.bookshelfhub.bookshelfhub

import android.content.Context
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.TimerUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun getTimerUtil(): TimerUtil {
        return TimerUtil(1000L)
    }

    @ViewModelScoped
    @Provides
    fun getConnectionUtil(@ApplicationContext context: Context): ConnectionUtil {
        return ConnectionUtil(context)
    }

}