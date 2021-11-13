package com.tixon.reminders.di.module

import android.app.Application
import android.content.Context
import com.tixon.reminders.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = (application as App).applicationContext
}
